package net.nekomura.utils.jixiv.artworks;

import net.nekomura.utils.jixiv.*;
import net.nekomura.utils.jixiv.enums.artwork.PixivArtworkType;
import net.nekomura.utils.jixiv.enums.comment.CommentType;
import net.nekomura.utils.jixiv.enums.comment.Stamp;
import net.nekomura.utils.jixiv.exception.PixivException;
import net.nekomura.utils.jixiv.utils.PixivUrlBuilder;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Novel {
    /**
     * 獲取小說資訊物件
     * @param id 小說id
     * @return 小說資訊物件
     * @throws IOException 獲取網路資料失敗
     */
    public static NovelInfo getInfo(int id) throws IOException {
        return new NovelInfo(id, Artwork.getArtworkData(id, PixivArtworkType.NOVELS));
    }

    /**
     * 喜歡小說
     * @param id 小說id
     * @return 是否在執行之前就已經喜歡過了
     * @throws IOException POST失敗
     */
    public static boolean like(int id) throws IOException {
        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("ajax/novels/like");
        String url = pub.build();

        JSONObject postData = new JSONObject();
        postData.put("novel_id", String.valueOf(id));

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = RequestBody.create(postData.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request.Builder rb = new Request.Builder();

        rb.url(url);
        rb.post(body);

        rb.addHeader("referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());
        rb.addHeader("x-csrf-token", Pixiv.getToken());

        Request request = rb.build();

        Response response = okHttpClient.newCall(request).execute();

        JSONObject json = new JSONObject(response.body().string());

        if (json.getBoolean("error")) {
            throw new PixivException(json.getString("message"));
        }

        return json.getJSONObject("body").getBoolean("is_liked");
    }

    /**
     * 收藏小說
     * @param id 小說id
     * @return 收藏id。如果已經收藏過了則返回null。
     * @throws IOException POST失敗
     */
    public static String addBookmark(int id) throws IOException {
        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("ajax/novels/bookmarks/add");
        String url = pub.build();
        JSONObject postData = new JSONObject();
        postData.put("novel_id", String.valueOf(id));
        postData.put("restrict", 0);
        postData.put("comment", "");
        postData.put("tags", new JSONArray());

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = RequestBody.create(postData.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request.Builder rb = new Request.Builder();

        rb.url(url);
        rb.post(body);

        rb.addHeader("referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());
        rb.addHeader("x-csrf-token", Pixiv.getToken());

        Request request = rb.build();

        Response response = okHttpClient.newCall(request).execute();

        JSONObject json = new JSONObject(response.body().string());

        if (json.getBoolean("error")) {
            throw new PixivException(json.getString("message"));
        }

        return json.getString("body");
    }

    /**
     * 取消收藏小說
     * @param id 小說id
     * @return HTTP狀態碼
     * @throws IOException POST失敗
     */
    public static int removeBookmark(int id) throws IOException {
        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("novel/bookmark_setting.php");
        String url = pub.build();
        String rest;

        OkHttpClient okHttpClient = new OkHttpClient();

        if (Novel.getInfo(id).isBookmarkPrivate()) {
            rest = "hide";
        }else {
            rest = "show";
        }

        RequestBody body = new FormBody.Builder()
                .add("tt", Pixiv.getToken())
                .add("p", "1")
                .add("untagged", String.valueOf(id))
                .add("rest", rest)
                .add("book_id[]", String.valueOf(Novel.getInfo(id).getBookmarkId()))
                .add("del", "1").build();

        Request.Builder rb = new Request.Builder().url(url);

        rb.method("POST", body);

        rb.addHeader("referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());
        rb.addHeader("x-csrf-token", Pixiv.getToken());

        Request request = rb.build();

        Response response = okHttpClient.newCall(request).execute();
        return response.code();
    }

    /**
     * 獲取小說評論
     * @param id 小說id
     * @param offset 抵銷最新幾個評論。為0則不抵銷。
     * @param limit 評論數目上限
     * @return 小說指定位置評論
     * @throws IOException GET失敗
     */
    public static Comments getComments(int id, int offset, int limit) throws IOException {
        List<Comment> comments = new ArrayList<>();
        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("ajax/novels/comments/roots");
        pub.addParameter("novel_id", id);
        pub.addParameter("offset", offset);
        pub.addParameter("limit", limit);
        String url = pub.build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        JSONObject commentJson = new JSONObject(Objects.requireNonNull(res.body()).string());

        if (commentJson.getBoolean("error")) {
            throw new PixivException(commentJson.getString("message"));
        }

        for (int i = 0; i < commentJson.getJSONObject("body").getJSONArray("comments").length(); i++) {
            comments.add(new Comment(commentJson.getJSONObject("body").getJSONArray("comments").getJSONObject(i), PixivArtworkType.NOVELS));
        }

        return new Comments(comments, commentJson.getJSONObject("body").getBoolean("hasNext"));
    }

    /**
     * 獲取小說所有評論
     * @param id 小說id
     * @return 含有所有評論的評論物件
     * @throws IOException GET失敗
     */
    public static Comments getComments(int id) throws IOException {
        return getComments(id, 0, getInfo(id).getCommentCount());
    }

    /**
     * 發文字評論
     * @param id 小說id
     * @param comment 評論內容
     * @return 評論id
     * @throws IOException POST失敗
     */
    public static int comment(int id, String comment) throws IOException {
        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("rpc/post_comment.php");
        String url = pub.build();

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("type", CommentType.COMMENT.toString().toLowerCase())
                .add("novel_id", String.valueOf(id))
                .add("author_user_id", String.valueOf(Novel.getInfo(id).getAuthorID()))
                .add("comment", comment).build();

        Request.Builder rb = new Request.Builder().url(url);

        rb.method("POST", body);

        rb.addHeader("referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());
        rb.addHeader("x-csrf-token", Pixiv.getToken());

        Request request = rb.build();

        Response response = okHttpClient.newCall(request).execute();
        return Integer.parseInt(new JSONObject(response.body().string()).getJSONObject("body").getString("comment_id"));
    }

    /**
     * 發表情圖貼評論
     * @param id 小說id
     * @param stamp 圖貼id
     * @return 評論id
     * @throws IOException POST失敗
     */
    public static int comment(int id, Stamp stamp) throws IOException {
        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("rpc/post_comment.php");
        String url = pub.build();

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("type", CommentType.STAMP.toString().toLowerCase())
                .add("novel_id", String.valueOf(id))
                .add("author_user_id", String.valueOf(Novel.getInfo(id).getAuthorID()))
                .add("stamp_id", String.valueOf(stamp.getId())).build();

        Request.Builder rb = new Request.Builder().url(url);

        rb.method("POST", body);

        rb.addHeader("referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());
        rb.addHeader("x-csrf-token", Pixiv.getToken());

        Request request = rb.build();

        Response response = okHttpClient.newCall(request).execute();
        return Integer.parseInt(new JSONObject(response.body().string()).getJSONObject("body").getString("comment_id"));
    }
}

package net.nekomura.utils.jixiv.artworks;

import net.nekomura.utils.jixiv.*;
import net.nekomura.utils.jixiv.enums.artwork.PixivArtworkType;
import net.nekomura.utils.jixiv.enums.comment.CommentType;
import net.nekomura.utils.jixiv.enums.comment.Stamp;
import net.nekomura.utils.jixiv.exception.PixivException;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Illustration {
    /**
     * 獲取插畫資訊物件
     * @param id 插畫id
     * @return 插畫資訊物件
     * @throws IOException 獲取網路資料失敗
     */
    public static IllustrationInfo getInfo(int id) throws IOException {
        return new IllustrationInfo(id, Artwork.getArtworkData(id, PixivArtworkType.ILLUSTS));
    }

    /**
     * 喜歡插畫
     * @param id 插畫id
     * @return 是否在執行之前就已經喜歡過了
     * @throws IOException POST失敗
     */
    public static boolean like(int id) throws IOException {
        String url = "https://www.pixiv.net/ajax/illusts/like";

        JSONObject postData = new JSONObject();
        postData.put("illust_id", String.valueOf(id));

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
     * 收藏插畫
     * @param id 插畫id
     * @return 收藏id
     * @throws IOException POST失敗
     */
    public static long addBookmark(int id) throws IOException {
        String url = "https://www.pixiv.net/ajax/illusts/bookmarks/add";
        JSONObject postData = new JSONObject();
        postData.put("illust_id", String.valueOf(id));
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
        return Long.parseLong(json.getJSONObject("body").getString("last_bookmark_id"));
    }

    /**
     * 取消收藏插畫
     * @param id 插畫id
     * @return HTTP狀態碼
     * @throws IOException POST失敗
     */
    public static int removeBookmark(int id) throws IOException {
        String url = "https://www.pixiv.net/rpc/index.php";

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("mode", "delete_illust_bookmark")
                .add("bookmark_id", String.valueOf(Illustration.getInfo(id).getBookmarkId()))
                .build();

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
     * 獲取插畫評論
     * @param id 插畫id
     * @param offset 抵銷最新幾個評論。為0則不抵銷。
     * @param limit 獲取評論數目上限
     * @return 插畫指定位置評論
     * @throws IOException GET失敗
     */
    public static Comments getComments(int id, int offset, int limit) throws IOException {
        List<Comment> comments = new ArrayList<>();
        String url = "https://www.pixiv.net/ajax/illusts/comments/roots?illust_id=" + id +"&offset=" + offset + "&limit=" + limit;

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
            comments.add(new Comment(commentJson.getJSONObject("body").getJSONArray("comments").getJSONObject(i), PixivArtworkType.ILLUSTS));
        }

        return new Comments(comments, commentJson.getJSONObject("body").getBoolean("hasNext"));
    }

    /**
     * 獲取插畫所有評論
     * @param id 插畫id
     * @return 含有所有評論的評論物件
     * @throws IOException GET失敗
     */
    public static Comments getComments(int id) throws IOException {
        return getComments(id, 0, getInfo(id).getCommentCount());
    }

    /**
     * 發文字評論
     * @param id 插畫id
     * @param comment 評論內容
     * @return 評論id
     * @throws IOException POST失敗
     */
    public static int comment(int id, String comment) throws IOException {
        String url = "https://www.pixiv.net/rpc/post_comment.php";

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("type", CommentType.COMMENT.toString().toLowerCase())
                .add("illust_id", String.valueOf(id))
                .add("author_user_id", String.valueOf(Illustration.getInfo(id).getAuthorID()))
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
     * @param id 插畫id
     * @param stamp 圖貼id
     * @return 評論id
     * @throws IOException POST失敗
     */
    public static int comment(int id, Stamp stamp) throws IOException {
        String url = "https://www.pixiv.net/rpc/post_comment.php";

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("type", CommentType.STAMP.toString().toLowerCase())
                .add("illust_id", String.valueOf(id))
                .add("author_user_id", String.valueOf(Illustration.getInfo(id).getAuthorID()))
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

package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.enums.artwork.PixivArtworkType;
import net.nekomura.utils.jixiv.exception.PixivException;
import net.nekomura.utils.jixiv.utils.UserAgentUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Artwork {

    private String phpSession;
    private String userAgent;

    public Artwork(String phpSession) {
        this.phpSession = phpSession;
    }

    public Artwork(String phpSession, String userAgent) {
        this.phpSession = phpSession;
        this.userAgent = userAgent;
    }

    public void setPhpSession(String phpSession) {
        this.phpSession = phpSession;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getPhpSession() {
        return phpSession;
    }

    public String getUserAgent() {
        return userAgent;
    }

    private String userAgent() {
        if (userAgent == null || userAgent.isEmpty()) {
            return UserAgentUtils.random();
        }else {
            return userAgent;
        }
    }

    private JSONObject getArtworkData(int id) throws IOException {
        String url;
        if (this instanceof Illustration) {
            url = "https://www.pixiv.net/ajax/illust/" + id;
        }else if (this instanceof Novel) {
            url = "https://www.pixiv.net/ajax/novel/" + id;
        }else {
            throw new IllegalArgumentException("The variable must be a Illustration or a Novel");
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        JSONObject json = new JSONObject(res.body().string());

        if (json.getBoolean("error")) {
            throw new PixivException(json.getString("message"));
        }
        return json;
    }

    private String getToken() throws IOException {
        String url = "https://www.pixiv.net/setting_user.php";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String html = res.body().string();

        if (html.contains("<meta name=\"global-data\" id=\"meta-global-data\" content='{\"token\":\"")) {
            int index1 = html.indexOf("<meta name=\"global-data\" id=\"meta-global-data\" content='{\"token\":\"");
            int length = "<meta name=\"global-data\" id=\"meta-global-data\" content='{\"token\":\"".length();
            String sub = html.substring(index1 + length);
            int index2 = sub.indexOf("\"");
            return html.substring(index1 + length, index1 + length + index2);
        }else {
            throw new PixivException("Cannot get pixiv token");
        }
    }

    private String getBookId(int id, String type) throws IOException {
        String url;
        if (type.equals("illust")) {
            url = "https://www.pixiv.net/bookmark_add.php?illust_id=" + id + "&type=illust";
        }else {
            url = "https://www.pixiv.net/novel/bookmark_add.php?id=" + id;
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String html = res.body().string();

        if (html.contains("<input type=\"hidden\" name=\"book_id[]\" value=\"")) {
            int index1 = html.indexOf("<input type=\"hidden\" name=\"book_id[]\" value=\"");
            int length = "<input type=\"hidden\" name=\"book_id[]\" value=\"".length();
            String sub = html.substring(index1 + length);
            int index2 = sub.indexOf("\"");
            return html.substring(index1 + length, index1 + length + index2);
        }else {
            throw new PixivException("Cannot get Book ID");
        }
    }

    private String getBookmarkRestriction(int id) throws IOException {
        String url;
        if (this instanceof Illustration) {
            url = "https://www.pixiv.net/bookmark_add.php?illust_id=" + id + "&type=illust";
        }else if (this instanceof Novel) {
            url = "https://www.pixiv.net/novel/bookmark_add.php?id=" + id;
        }else {
            throw new IllegalArgumentException("The variable must be a Illustration or a Novel");
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String html = res.body().string();

        if (html.contains("<input type=\"hidden\" name=\"rest\" value=\"")) {
            int index1 = html.indexOf("<input type=\"hidden\" name=\"rest\" value=\"");
            int length = "<input type=\"hidden\" name=\"rest\" value=\"".length();
            String sub = html.substring(index1 + length);
            int index2 = sub.indexOf("\"");
            return html.substring(index1 + length, index1 + length + index2);
        }else {
            throw new PixivException("Cannot get Bookmark Restriction");
        }
    }


    /**
     * 獲取作品資訊物件
     * @deprecated 請使用 {@link #getInfo(int)}
     * @param id 作品id
     * @return 作品資訊物件
     * @throws IOException 讀取網路資料失敗
     */
    @Deprecated
    public ArtworkInfo get(int id) throws IOException {
        return getInfo(id);
    }

    /**
     * 獲取作品資訊物件
     * @param id 作品id
     * @return 作品資訊物件
     * @throws IOException 讀取網路資料失敗
     */
    public ArtworkInfo getInfo(int id) throws IOException {
        return new ArtworkInfo(id, getArtworkData(id));
    }

    /**
     * 喜歡作品
     * @param id 作品id
     * @return HTTP狀態碼
     * @throws IOException 讀取網路資料失敗
     */
    public int like(int id) throws IOException {
        String url;
        JSONObject postData = new JSONObject();
        if (this instanceof Illustration) {
            url = "https://www.pixiv.net/ajax/illusts/like";
            postData.put("illust_id", String.valueOf(id));
        }else if (this instanceof Novel) {
            url = "https://www.pixiv.net/ajax/novels/like";
            postData.put("novel_id", String.valueOf(id));
        }else {
            throw new IllegalArgumentException("The variable must be a Illustration or a Novel");
        }

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = RequestBody.create(postData.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request.Builder rb = new Request.Builder();

        rb.url(url);
        rb.post(body);

        rb.addHeader("referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());
        rb.addHeader("x-csrf-token", getToken());

        Request request = rb.build();

        Response response = okHttpClient.newCall(request).execute();

        JSONObject json = new JSONObject(response.body().string());

        if (json.getBoolean("error")) {
            throw new PixivException(json.getString("message"));
        }

        return response.code();
    }

    /**
     * 收藏作品
     * @param id 作品ID
     * @return HTTP狀態碼
     * @throws IOException 讀取網路資料失敗
     */
    public int addBookmark(int id) throws IOException {
        String url;
        JSONObject postData = new JSONObject();
        if (this instanceof Illustration) {
            url = "https://www.pixiv.net/ajax/illusts/bookmarks/add";
            postData.put("illust_id", String.valueOf(id));
            postData.put("restrict", 0);
            postData.put("comment", "");
            postData.put("tags", new JSONArray());
        }else if (this instanceof Novel) {
            url = "https://www.pixiv.net/ajax/novels/bookmarks/add";
            postData.put("novel_id", String.valueOf(id));
            postData.put("restrict", 0);
            postData.put("comment", "");
            postData.put("tags", new JSONArray());
        }else {
            throw new IllegalArgumentException("The variable must be a Illustration or a Novel");
        }

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = RequestBody.create(postData.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request.Builder rb = new Request.Builder();

        rb.url(url);
        rb.post(body);

        rb.addHeader("referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());
        rb.addHeader("x-csrf-token", getToken());

        Request request = rb.build();

        Response response = okHttpClient.newCall(request).execute();

        JSONObject json = new JSONObject(response.body().string());

        if (json.getBoolean("error")) {
            throw new PixivException(json.getString("message"));
        }

        return response.code();
    }

    /**
     * 取消收藏作品
     * @param id 作品ID
     * @return HTTP狀態碼
     * @throws IOException 讀取網路資料失敗
     */
    public int removeBookmark(int id) throws IOException {
        String url;
        String type = "";
        if (this instanceof Illustration) {
            url = "https://www.pixiv.net/bookmark_setting.php";
            type = "illust";
        }else if (this instanceof Novel) {
            url = "https://www.pixiv.net/novel/bookmark_setting.php";
        }else {
            throw new IllegalArgumentException("The variable must be a Illustration or a Novel");
        }

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("tt", getToken())
                .add("p", "1")
                .add("untagged", String.valueOf(id))
                .add("rest", getBookmarkRestriction(id))
                .add("book_id[]", getBookId(id, type))
                .add("del", "1").build();

        Request.Builder rb = new Request.Builder().url(url);

        rb.method("POST", body);

        rb.addHeader("referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());
        rb.addHeader("x-csrf-token", getToken());

        Request request = rb.build();

        Response response = okHttpClient.newCall(request).execute();
        return response.code();
    }

    /**
     * 獲取作品評論
     * @param id 作品id
     * @return 作品所有評論
     * @throws IOException 網路資料獲取失敗
     */
    public List<Comment> getComments(int id) throws IOException {
        List<Comment> comments = new ArrayList<>();
        String url;
        PixivArtworkType type;

        if (this instanceof Illustration) {
            url = "https://www.pixiv.net/ajax/illusts/comments/roots?illust_id=" + id +"&offset=0&limit=" + this.getInfo(id).getCommentCount();
            type = PixivArtworkType.Illusts;
        }else if (this instanceof Novel) {
            url = "https://www.pixiv.net/ajax/novels/comments/roots?novel_id=" + id + "&offset=0&limit=" + this.getInfo(id).getCommentCount();
            type = PixivArtworkType.Novels;
        }else {
            throw new IllegalArgumentException("The variable must be a Illustration or a Novel");
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        JSONObject commentJson = new JSONObject(Objects.requireNonNull(res.body()).string());

        if (commentJson.getBoolean("error")) {
            throw new PixivException(commentJson.getString("message"));
        }

        for (int i = 0; i < commentJson.getJSONObject("body").getJSONArray("comments").length(); i++) {
            comments.add(new Comment(commentJson.getJSONObject("body").getJSONArray("comments").getJSONObject(i), type, phpSession, userAgent()));
        }

        return comments;
    }
}
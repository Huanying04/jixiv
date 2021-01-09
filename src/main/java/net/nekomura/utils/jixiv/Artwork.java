package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.Utils.UserAgentUtils;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

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

    private JSONObject getArtworkData(int id) throws Exception {
        String url;
        if (this instanceof Illustration) {
            url = "https://www.pixiv.net/ajax/illust/" + id;
        }else if (this instanceof Novel) {
            url = "https://www.pixiv.net/ajax/novel/" + id;
        }else {
            throw new Exception("The variable must be a Illustration or a Novel");
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
            throw new Exception(json.getString("message"));
        }
        return json;
    }

    private String getToken() throws Exception {
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
            throw new Exception("Cannot get pixiv token");
        }
    }

    private String getBookId(int id, String type) throws Exception {
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
            throw new Exception("Cannot get Book ID []");
        }
    }

    private String getBookmarkRestriction(int id) throws Exception {
        String url;
        if (this instanceof Illustration) {
            url = "https://www.pixiv.net/bookmark_add.php?illust_id=" + id + "&type=illust";
        }else if (this instanceof Novel) {
            url = "https://www.pixiv.net/novel/bookmark_add.php?id=" + id;
        }else {
            throw new Exception("The variable must be a Illustration or a Novel");
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
            throw new Exception("Cannot get Bookmark Restriction");
        }
    }

    /**
     * 獲取作品資訊物件
     * @param id 作品id
     * @return 作品資訊物件
     * @throws Exception 作品不存在或沒有權限等，獲取失敗
     */
    public ArtworkInfo get(int id) throws Exception {
        return new ArtworkInfo(id, getArtworkData(id));
    }

    /**
     * 喜歡作品
     * @param id 作品id
     * @return HTTP狀態碼
     * @throws Exception
     */
    public int like(int id) throws Exception {
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
            throw new Exception(json.getString("message"));
        }

        return response.code();
    }

    /**
     * 收藏作品
     * @param id 作品ID
     * @return HTTP狀態碼
     * @throws Exception
     */
    public int addBookmark(int id) throws Exception {
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
            throw new Exception(json.getString("message"));
        }

        return response.code();
    }

    /**
     * 取消收藏作品
     * @param id 作品ID
     * @return HTTP狀態碼
     * @throws Exception
     */
    public int removeBookmark(int id) throws Exception {
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
}
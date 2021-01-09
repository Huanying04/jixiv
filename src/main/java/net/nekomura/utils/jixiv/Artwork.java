package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.Utils.UserAgentUtils;
import okhttp3.*;
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

    /**
     * 獲取作品資訊物件
     * @param id 作品id
     * @return 作品資訊物件
     * @throws Exception 作品不存在或沒有權限等，獲取失敗
     */
    public ArtworkInfo get(int id) throws Exception {
        return new ArtworkInfo(id, getArtworkData(id));
    }

    public void like(int id) throws Exception {
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

        Request request = rb.build();

        Response response = okHttpClient.newCall(request).execute();

        JSONObject json = new JSONObject(response.body().string());

        if (json.getBoolean("error")) {
            throw new Exception(json.getString("message"));
        }
    }

}
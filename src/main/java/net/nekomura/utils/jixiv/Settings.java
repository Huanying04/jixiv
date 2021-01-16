package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.exception.PixivException;
import net.nekomura.utils.jixiv.utils.UserAgentUtils;
import okhttp3.*;

import java.io.IOException;

public class Settings {
    private String phpSession;
    private String userAgent;

    public Settings(String phpSession, String userAgent) {
        this.phpSession = phpSession;
        this.userAgent = userAgent;
    }

    public Settings(String phpSession) {
        this.phpSession = phpSession;
    }

    private String userAgent() {
        if (userAgent == null || userAgent.isEmpty()) {
            return UserAgentUtils.random();
        }else {
            return userAgent;
        }
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

    private String getSubmit() throws IOException {
        String url = "https://www.pixiv.net/setting_user.php";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String html = res.body().string();

        if (html.contains("<input name=\"submit\" type=\"submit\" class=\"btn_type01\" value=\"")) {
            int index1 = html.indexOf("<input name=\"submit\" type=\"submit\" class=\"btn_type01\" value=\"");
            int length = "<input name=\"submit\" type=\"submit\" class=\"btn_type01\" value=\"".length();
            String sub = html.substring(index1 + length);
            int index2 = sub.indexOf("\"");
            return html.substring(index1 + length, index1 + length + index2);
        }else {
            throw new PixivException("Cannot get pixiv submit string");
        }
    }

    private String getLanguage() throws IOException {
        String url = "https://www.pixiv.net/setting_user.php";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String html = res.body().string();

        if (html.contains(",lang: \"")) {
            int index1 = html.indexOf(",lang: \"");
            int length = ",lang: \"".length();
            String sub = html.substring(index1 + length);
            int index2 = sub.indexOf("\"");
            return html.substring(index1 + length, index1 + length + index2);
        }else {
            throw new PixivException("Cannot get user language");
        }
    }

    /**
     * 設定作品瀏覽限制
     * @param r18Restriction 是否可瀏覽R18作品
     * @param r18gRestriction 是否可瀏覽R18-G作品
     * @return HTTP 狀態碼
     * @throws IOException 讀取網路資料失敗或修改失敗
     */
    public int setViewRestriction(boolean r18Restriction, boolean r18gRestriction) throws IOException {
            String url = "https://www.pixiv.net/setting_user.php";

            OkHttpClient okHttpClient = new OkHttpClient();

            String r18;
            if (r18Restriction) {
                r18 = "show";
            }else {
                r18 = "hide";
            }

            String r18g;
            if (r18gRestriction) {
                r18g = "2";
            }else {
                r18g = "1";
            }

            RequestBody body = new FormBody.Builder()
                                .add("mode", "mod")
                                .add("tt", getToken())
                                .add("r18", r18)
                                .add("r18g", r18g)
                                .add("user_language", getLanguage())
                                .addEncoded("submit", getSubmit()).build();

            Request.Builder rb = new Request.Builder().url(url);

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

            rb.method("POST", body);


            rb.addHeader("referer", "https://www.pixiv.net");
            rb.addHeader("cookie", "PHPSESSID=" + phpSession);
            rb.addHeader("user-agent", userAgent());

            Request request = rb.build();

            Response response = okHttpClient.newCall(request).execute();
            return response.code();
    }
}

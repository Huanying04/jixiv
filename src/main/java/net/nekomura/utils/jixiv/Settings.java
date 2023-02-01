package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.exception.PixivException;
import net.nekomura.utils.jixiv.enums.settings.Language;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class Settings {
    private static String getSubmit() throws IOException {
        String url = "https://www.pixiv.net/setting_user.php";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

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

    private static String getLanguage() throws IOException {
        String url = "https://www.pixiv.net/setting_user.php";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

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
    public static int setViewRestriction(boolean r18Restriction, boolean r18gRestriction) throws IOException {
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
                                .add("tt", Pixiv.getToken())
                                .add("r18", r18)
                                .add("r18g", r18g)
                                .add("user_language", getLanguage())
                                .addEncoded("submit", getSubmit()).build();

            Request.Builder rb = new Request.Builder().url(url);

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

            rb.method("POST", body);


            rb.addHeader("referer", "https://www.pixiv.net");
            rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
            rb.addHeader("user-agent", Jixiv.userAgent());

            Request request = rb.build();

            Response response = okHttpClient.newCall(request).execute();
            return response.code();
    }

    /**
     * 設定語言
     * @param language 語言
     * @return 返回訊息
     * @throws IOException 讀取網路資料失敗或修改失敗
     */
    public static String setLanguage(Language language) throws IOException {
        String url = "https://www.pixiv.net/ajax/user/language";
        JSONObject postData = new JSONObject();
        postData.put("code", language.getLanguageCode());

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
        return json.getString("message");
    }
}

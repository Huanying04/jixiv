package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.Utils.UserAgentUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class PixivArtwork {

    private String phpSession;
    private String userAgent;

    public PixivArtwork(String phpSession) {
        this.phpSession = phpSession;
    }

    public PixivArtwork(String phpSession, String userAgent) {
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
        if (userAgent == null ||userAgent.isEmpty()) {
            return UserAgentUtils.getRandomUserAgent();
        }else {
            return userAgent;
        }
    }

    /**
     * 獲取網頁頁面的HTML
     * @param id 作品ID
     */
    @NotNull
    public String getArtworkPageHtml(int id) throws Exception {
        String url;
        if (this instanceof PixivIllustration) {
            url = "https://www.pixiv.net/artworks/" + id;
        }else if (this instanceof PixivNovel) {
            url = "https://www.pixiv.net/novel/show.php?id=" + id;
        }else {
            throw new Exception("The variable must be a PixivImage or a net.nekomura.utils.jixiv.PixivNovel.");
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        return res.body().string();
    }

    private JSONObject getArtworkPreloadData(int id) throws Exception {
        String html = this.getArtworkPageHtml(id);
        if (html.contains("あなたの環境からはpixivにアクセスできません。")) {
            throw new IllegalAccessException("There's something wrong with your PHP Session.");
        }
        String from = "<meta name=\"preload-data\" id=\"meta-preload-data\" content='";
        String to = "'>";

        int fromIndex = html.indexOf(from);
        if (fromIndex == -1)
            throw new IllegalArgumentException("Work has been deleted or the ID does not exist.");
        int toIndex = html.indexOf(to, fromIndex);
        String targetJsonString = html.subSequence(fromIndex + from.length(), toIndex).toString().replace(from, "");
        return new JSONObject(targetJsonString);
    }

    /**
     * 獲取作品資訊物件
     * @param id 作品id
     * @return 作品資訊物件
     * @throws Exception
     */
    public PixivArtworkInfo get(int id) throws Exception {
        return new PixivArtworkInfo(id, getArtworkPreloadData(id));
    }

}

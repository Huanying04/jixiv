package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.Utils.UserAgentUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class PixivMyAccount {

    private String phpSession;
    private String userAgent;

    public PixivMyAccount(String phpSession) {
        this.phpSession = phpSession;
    }

    public PixivMyAccount(String phpSession, String userAgent) {
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
        if (userAgent.isEmpty()) {
            return UserAgentUtils.getRandomUserAgent();
        }else {
            return userAgent;
        }
    }

    public PixivFollowingNewWork getFollowingNewWorks(int page) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url("https://www.pixiv.net/bookmark_new_illust.php");

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String html = Objects.requireNonNull(res.body()).string();

        String from = "<div id=\"js-mount-point-latest-following\"data-items=\"";
        String to = "\"";

        int fromIndex = html.indexOf(from);
        int toIndex = html.indexOf(to, fromIndex);
        String target = html.subSequence(fromIndex + from.length(), toIndex).toString().replace(from, "");
        return new PixivFollowingNewWork(target);
    }

}
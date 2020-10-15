import Utils.UserAgentUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class PixivMyAccount {

    private String phpSession;

    public PixivMyAccount(String phpSession) {
        this.phpSession = phpSession;
    }

    public void setPhpSession(String phpSession) {
        this.phpSession = phpSession;
    }

    public PixivFollowingNewWork getFollowingNewWorks(int page) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url("https://www.pixiv.net/bookmark_new_illust.php");

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", UserAgentUtils.getRandomUserAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String html = res.body().string();

        String from = "<div id=\"js-mount-point-latest-following\"data-items=\"";
        String to = "\"";

        int fromIndex = html.indexOf(from);
        int toIndex = html.indexOf(to, fromIndex);
        String target = html.subSequence(fromIndex + from.length(), toIndex).toString().replace(from, "");
        return new PixivFollowingNewWork(target);
    }

}
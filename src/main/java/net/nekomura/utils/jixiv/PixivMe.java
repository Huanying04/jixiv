package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.Utils.UserAgentUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.util.Objects;

public class PixivMe {

    private String phpSession;
    private String userAgent;

    public PixivMe(String phpSession) {
        this.phpSession = phpSession;
    }

    public PixivMe(String phpSession, String userAgent) {
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
            return UserAgentUtils.random();
        }else {
            return userAgent;
        }
    }

    /**
     * 已關注用戶的最新作品
     * @param page 頁碼
     * @return 關注用戶的最新作品資訊物件
     * @throws IOException 獲取失敗
     */
    public FollowingNewWork getFollowingNewWorks(int page) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url("https://www.pixiv.net/bookmark_new_illust.php?p=" + page);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String html = Objects.requireNonNull(res.body()).string();

        String from = "<div id=\"js-mount-point-latest-following\"data-items=\"";
        String to = "\"style=\"min-height: 1460px;\"></div>";

        int fromIndex = html.indexOf(from);
        int toIndex = html.indexOf(to, fromIndex);
        String target = html.subSequence(fromIndex, toIndex).toString().replace(from, "");
        target = StringEscapeUtils.unescapeHtml4(target);
        return new FollowingNewWork(page, target);
    }
}
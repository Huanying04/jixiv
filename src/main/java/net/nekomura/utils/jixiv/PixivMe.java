package net.nekomura.utils.jixiv;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.util.Objects;

@Deprecated
public class PixivMe {
    /**
     * 已關注用戶的最新作品
     * @deprecated 請使用 {@link Pixiv#getFollowingNewWorks(int)}
     * @param page 頁碼
     * @return 關注用戶的最新作品資訊物件
     * @throws IOException 獲取失敗
     */
    @Deprecated
    public static FollowingNewWork getFollowingNewWorks(int page) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url("https://www.pixiv.net/bookmark_new_illust.php?p=" + page);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

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
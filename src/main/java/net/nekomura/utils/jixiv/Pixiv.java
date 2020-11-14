package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.Enums.*;
import net.nekomura.utils.jixiv.Utils.UserAgentUtils;
import com.google.common.net.UrlEscapers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Pixiv {

    private String phpSession;
    private String userAgent;

    public Pixiv(String phpSession) {
        this.phpSession = phpSession;
    }

    public Pixiv(String phpSession, String userAgent) {
        this.phpSession = phpSession;
        this.userAgent = userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setPhpSession(String phpSession) {
        this.phpSession = phpSession;
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

    private JSONObject getUserProfile(int id) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url("https://www.pixiv.net/ajax/user/" + id + "/profile/all");

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();
        res.close();
        return new JSONObject(Objects.requireNonNull(res.body()).string());
    }

    private JSONObject getUserPreloadData(int id) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url("https://www.pixiv.net/users/" + id);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();
        res.close();

        String from = "<meta name=\"preload-data\" id=\"meta-preload-data\" content='";
        String to = "'>";

        String html = Objects.requireNonNull(res.body()).string();

        int fromIndex = html.indexOf(from);
        if (fromIndex == -1)
            throw new IllegalArgumentException("Work has been deleted or the ID does not exist.");
        int toIndex = html.indexOf(to, fromIndex);
        String targetJsonString = html.subSequence(fromIndex + from.length(), toIndex).toString().replace(from, "");
        return new JSONObject(targetJsonString);
    }

    /**
     * 獲取用戶資料物件
     * @param id 使用者id
     * @return 用戶資料物件
     * @throws IOException 獲取失敗
     */
    public User getUserInfo(int id) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url("https://www.pixiv.net/ajax/user/" + id + "/profile/all");

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();
        res.close();

        return new User(id, getUserProfile(id), getUserPreloadData(id));
    }

    /**
     * 下載用戶所有插圖及漫畫作品
     * @param folder 資料夾位置
     * @param userId 用戶id
     * @param size 圖片大小
     * @throws Exception 獲取失敗
     */
    public void downloadUserAllIllustration(File folder, int userId, PixivImageSize size) throws Exception {
        int[] artworks = getUserInfo(userId).getUserArtworks(PixivArtworkType.Illusts);
        for (int id: artworks) {
            new Illustration(phpSession).get(id).downloadAll(folder, size);
        }
    }

    /**
     * 下載用戶所有插圖及漫畫作品
     * @param folderPath 資料夾位置
     * @param userId 用戶id
     * @param size 圖片大小
     * @throws Exception 獲取失敗
     */
    public void downloadUserAllIllustration(String folderPath, int userId, PixivImageSize size) throws Exception {
        int[] artworks = getUserInfo(userId).getUserArtworks(PixivArtworkType.Illusts);
        for (int id: artworks) {
            new Illustration(phpSession).get(id).downloadAll(folderPath, size);
        }
    }

    /**
     * 獲取排行榜
     * @param page 頁碼
     * @param mode 篩選模式
     * @param content 作品類別
     * @param date 排行榜日期
     * @return 排行榜物件
     * @throws IOException 獲取失敗
     */
    public Rank rank(int page, PixivRankMode mode, @NotNull PixivRankContent content, String date) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url;

        if (content.equals(PixivRankContent.Overall)) {
            url = "https://www.pixiv.net/ranking.php?mode=" + mode.toString().toLowerCase() + "&date=" + date + "&p=" + page + "&format=json";
        }else {
            url = "https://www.pixiv.net/ranking.php?mode=" + mode.toString().toLowerCase() + "&content=" + content.toString().toLowerCase() + "&date=" + date + "&p=" + page + "&format=json";
        }

        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        return new Rank(page, mode, content, date, Objects.requireNonNull(res.body()).string(), phpSession, userAgent());
    }

    /**
     * 獲取排行榜
     * @param page 頁碼
     * @param mode 篩選模式
     * @param content 作品類別
     * @return 排行榜物件
     * @throws IOException 獲取失敗
     */
    public Rank rank(int page, PixivRankMode mode, @NotNull PixivRankContent content) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url;

        if (content.equals(PixivRankContent.Overall)) {
            url = "https://www.pixiv.net/ranking.php?mode=" + mode.toString().toLowerCase() + "&p=" + page + "&format=json";
        }else {
            url = "https://www.pixiv.net/ranking.php?mode=" + mode.toString().toLowerCase() + "&content=" + content.toString().toLowerCase() + "&p=" + page + "&format=json";
        }

        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        return new Rank(page, mode, content, null, Objects.requireNonNull(res.body()).string(), phpSession, userAgent());
    }

    /**
     * 獲取排行榜
     * @param page 頁碼
     * @return 排行榜物件
     * @throws IOException 獲取失敗
     */
    public Rank rank(int page) throws IOException {
        return rank(page, PixivRankMode.Daily, PixivRankContent.Overall);
    }

    /**
     * 搜尋
     * @param keywords 關鍵字
     * @param page 頁碼
     * @param artistType 搜尋作品類別
     * @param order 排序方式
     * @param mode 搜尋作品年齡分類
     * @param sMode 關鍵字搜尋方式
     * @param type 搜尋作品類別
     * @return 搜尋結果物件
     * @throws IOException 獲取失敗
     */
    public SearchResult search(String keywords, int page, @NotNull PixivSearchArtworkType artistType, PixivSearchOrder order, @NotNull PixivSearchMode mode, @NotNull PixivSearchSMode sMode, @NotNull PixivSearchType type) throws IOException {
        String url = String.format("https://www.pixiv.net/ajax/search/%s/%s?word=%s&order=%s&p=%d&s_mode=%s&type=%s&lang=zh_tw",
                artistType.toString().toLowerCase(),
                UrlEscapers.urlFragmentEscaper().escape(keywords),
                UrlEscapers.urlFragmentEscaper().escape(keywords),
                mode.toString().toLowerCase(),
                page,
                sMode.toString().toLowerCase(),
                type.toString().toLowerCase());
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String resultType;
        switch (artistType) {
            case All:
                resultType = "illustManga";
                break;
            case Illustrations:
                resultType = "illust";
                break;
            case Manga:
                resultType = "manga";
                break;
            case Novels: resultType = "novel";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + artistType);
        }


        return new SearchResult(Objects.requireNonNull(res.body()).string(), resultType);
    }

    /**
     * 搜尋
     * @param keywords 關鍵字
     * @param page 頁碼
     * @return 搜尋結果物件
     * @throws IOException 獲取失敗
     */
    public SearchResult search(String keywords, int page) throws IOException {
        return search(keywords, page, PixivSearchArtworkType.Illustrations, PixivSearchOrder.NEW_TO_OLD, PixivSearchMode.SAFE, PixivSearchSMode.S_TAG, PixivSearchType.Illust);
    }
}
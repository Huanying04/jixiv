package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.artworks.Illustration;
import net.nekomura.utils.jixiv.enums.artwork.PixivArtworkType;
import net.nekomura.utils.jixiv.enums.artwork.PixivImageSize;
import net.nekomura.utils.jixiv.enums.rank.PixivRankContent;
import net.nekomura.utils.jixiv.enums.rank.PixivRankMode;
import net.nekomura.utils.jixiv.enums.search.*;
import net.nekomura.utils.jixiv.exception.PixivException;
import com.google.common.net.UrlEscapers;
import okhttp3.*;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Pixiv {
    private static JSONObject getUserProfile(int id) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url("https://www.pixiv.net/ajax/user/" + id + "/profile/all");

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        return new JSONObject(Objects.requireNonNull(res.body()).string());
    }

    private static JSONObject getUserPreloadData(int id) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url("https://www.pixiv.net/users/" + id);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

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
    public static User getUserInfo(int id) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url("https://www.pixiv.net/ajax/user/" + id + "/profile/all");

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        return new User(id, getUserProfile(id), getUserPreloadData(id));
    }

    public static String getToken() throws IOException {
        String url = "https://www.pixiv.net/setting_user.php";
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

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

    /**
     * 下載用戶所有插圖及漫畫作品
     * @param folder 資料夾位置
     * @param userId 用戶id
     * @param size 圖片大小
     * @throws IOException 獲取失敗
     */
    public static void downloadUserAllIllustration(File folder, int userId, PixivImageSize size) throws IOException {
        int[] artworks = getUserInfo(userId).getUserArtworks(PixivArtworkType.Illusts);
        for (int id: artworks) {
            Illustration.getInfo(id).downloadAll(folder, size);
        }
    }

    /**
     * 下載用戶所有插圖及漫畫作品
     * @param folderPath 資料夾位置
     * @param userId 用戶id
     * @param size 圖片大小
     * @throws IOException 讀取網路資料失敗
     */
    public static void downloadUserAllIllustration(String folderPath, int userId, PixivImageSize size) throws IOException {
        int[] artworks = getUserInfo(userId).getUserArtworks(PixivArtworkType.Illusts);
        for (int id: artworks) {
            Illustration.getInfo(id).downloadAll(folderPath, size);
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
    public static Rank rank(int page, PixivRankMode mode, @NotNull PixivRankContent content, String date) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url;

        if (content.equals(PixivRankContent.Overall)) {
            url = "https://www.pixiv.net/ranking.php?mode=" + mode.toString().toLowerCase() + "&date=" + date + "&p=" + page + "&format=json";
        }else {
            url = "https://www.pixiv.net/ranking.php?mode=" + mode.toString().toLowerCase() + "&content=" + content.toString().toLowerCase() + "&date=" + date + "&p=" + page + "&format=json";
        }

        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        return new Rank(page, mode, content, date, Objects.requireNonNull(res.body()).string());
    }

    /**
     * 獲取排行榜
     * @param page 頁碼
     * @param mode 篩選模式
     * @param content 作品類別
     * @return 排行榜物件
     * @throws IOException 獲取失敗
     */
    public static Rank rank(int page, PixivRankMode mode, @NotNull PixivRankContent content) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url;

        if (content.equals(PixivRankContent.Overall)) {
            url = "https://www.pixiv.net/ranking.php?mode=" + mode.toString().toLowerCase() + "&p=" + page + "&format=json";
        }else {
            url = "https://www.pixiv.net/ranking.php?mode=" + mode.toString().toLowerCase() + "&content=" + content.toString().toLowerCase() + "&p=" + page + "&format=json";
        }

        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        return new Rank(page, mode, content, null, Objects.requireNonNull(res.body()).string());
    }

    /**
     * 獲取排行榜
     * @param page 頁碼
     * @return 排行榜物件
     * @throws IOException 獲取失敗
     */
    public static Rank rank(int page) throws IOException {
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
    public static SearchResult search(String keywords, int page, @NotNull PixivSearchArtworkType artistType, PixivSearchOrder order, @NotNull PixivSearchMode mode, @NotNull PixivSearchSMode sMode, @NotNull PixivSearchType type) throws IOException {
        String url = String.format("https://www.pixiv.net/ajax/search/%s/%s?word=%s&order=%s&mode=%s&p=%d&s_mode=%s&type=%s&lang=zh_tw",
                artistType.toString().toLowerCase(),
                UrlEscapers.urlFragmentEscaper().escape(keywords),
                UrlEscapers.urlFragmentEscaper().escape(keywords),
                order.toString().toLowerCase(),
                mode.toString().toLowerCase(),
                page,
                sMode.toString().toLowerCase(),
                type.toString().toLowerCase());
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

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
    public static SearchResult search(String keywords, int page) throws IOException {
        return search(keywords, page, PixivSearchArtworkType.Illustrations, PixivSearchOrder.NEW_TO_OLD, PixivSearchMode.SAFE, PixivSearchSMode.S_TAG, PixivSearchType.Illust);
    }

    /**
     * 關注用戶
     * @param id 用戶ID
     * @return HTTP狀態碼
     * @throws IOException 讀取網路資料失敗
     */
    public static int addBookmarkUser(int id) throws IOException {
        String url = "https://www.pixiv.net/bookmark_add.php";

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("mode", "add")
                .add("type", "user")
                .add("user_id", String.valueOf(id))
                .add("tag", "")
                .add("restrict", "0")
                .addEncoded("format", "json").build();

        Request.Builder rb = new Request.Builder().url(url);

        rb.method("POST", body);

        rb.addHeader("referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());
        rb.addHeader("x-csrf-token", getToken());

        Request request = rb.build();

        Response response = okHttpClient.newCall(request).execute();
        return response.code();
    }

    /**
     * 取消關注用戶
     * @param id 用戶ID
     * @return HTTP狀態碼
     * @throws IOException 讀取網路資料失敗
     */
    public static int removeBookmarkUser(int id) throws IOException {
        String url = "https://www.pixiv.net/rpc_group_setting.php";

        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("mode", "del")
                .add("type", "bookuser")
                .add("id", String.valueOf(id)).build();

        Request.Builder rb = new Request.Builder().url(url);

        rb.method("POST", body);

        rb.addHeader("referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());
        rb.addHeader("x-csrf-token", getToken());

        Request request = rb.build();

        Response response = okHttpClient.newCall(request).execute();
        return response.code();
    }

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
        String to = "\"style=\"min-height";

        int fromIndex = html.indexOf(from);
        int toIndex = html.indexOf(to, fromIndex);
        String target = html.subSequence(fromIndex, toIndex).toString().replace(from, "");
        target = StringEscapeUtils.unescapeHtml4(target);
        return new FollowingNewWork(page, target);
    }

    public static JSONObject mainpageIllustrations() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url("https://www.pixiv.net/ajax/top/illust?mode=all");

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        JSONObject data = new JSONObject(Objects.requireNonNull(res.body()).string());

        if (data.getBoolean("error")) {
            throw new PixivException(data.getString("message"));
        }

        res.close();

        return data.getJSONObject("body");
    }
}
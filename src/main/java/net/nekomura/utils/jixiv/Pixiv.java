package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.artworks.Illustration;
import net.nekomura.utils.jixiv.enums.artwork.PixivArtworkType;
import net.nekomura.utils.jixiv.enums.artwork.PixivImageSize;
import net.nekomura.utils.jixiv.enums.rank.PixivRankContent;
import net.nekomura.utils.jixiv.enums.rank.PixivRankMode;
import net.nekomura.utils.jixiv.enums.search.*;
import net.nekomura.utils.jixiv.exception.PixivException;
import net.nekomura.utils.jixiv.utils.PixivUrlBuilder;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Objects;

public class Pixiv {
    private static JSONObject getUserProfile(int id) throws IOException {
        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("ajax/user/" + id + "/profile/all");

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(pub.build());

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        return new JSONObject(Objects.requireNonNull(res.body()).string());
    }

    private static JSONObject getUserData(int id) throws IOException {
        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("ajax/user/" + id + "/profile/all");
        pub.addParameter("full", "1");

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(pub.build());

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        return new JSONObject(Objects.requireNonNull(res.body()).string());
    }

    /**
     * 獲取用戶資料物件
     * @param id 使用者id
     * @return 用戶資料物件
     * @throws IOException 獲取失敗
     */
    public static User getUserInfo(int id) throws IOException {
        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("ajax/user/" + id + "/profile/all");

        Request.Builder rb = new Request.Builder().url(pub.build());

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        return new User(id, getUserProfile(id), getUserData(id));
    }

    public static String getToken() throws IOException {
        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("setting_user.php");
        String url = pub.build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String html = Objects.requireNonNull(res.body()).string();

        res.close();

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
        int[] artworks = getUserInfo(userId).getUserArtworks(PixivArtworkType.ILLUSTS);
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
        int[] artworks = getUserInfo(userId).getUserArtworks(PixivArtworkType.ILLUSTS);
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
        PixivUrlBuilder pub = new PixivUrlBuilder();

        if (content.equals(PixivRankContent.OVERALL)) {
            pub.setPath("ranking.php");
            pub.addParameter("mode", mode);
            pub.addParameter("date", date);
            pub.addParameter("p", page);
            pub.addParameter("format", "json");
            url = pub.build();
        }else {
            pub.setPath("ranking.php");
            pub.addParameter("mode", mode);
            pub.addParameter("content", content);
            pub.addParameter("date", date);
            pub.addParameter("p", page);
            pub.addParameter("format", "json");
            url = pub.build();
        }

        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String json = Objects.requireNonNull(res.body()).string();

        res.close();

        return new Rank(page, mode, content, date, json);
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
        PixivUrlBuilder pub = new PixivUrlBuilder();

        if (content.equals(PixivRankContent.OVERALL)) {
            pub.setPath("ranking.php");
            pub.addParameter("mode", mode);
            pub.addParameter("p", page);
            pub.addParameter("format", "json");
            url = pub.build();
        }else {
            pub.setPath("ranking.php");
            pub.addParameter("mode", mode);
            pub.addParameter("content", content);
            pub.addParameter("p", page);
            pub.addParameter("format", "json");
            url = pub.build();
        }

        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String json = Objects.requireNonNull(res.body()).string();

        res.close();

        return new Rank(page, mode, content, null, json);
    }

    /**
     * 獲取排行榜
     * @param page 頁碼
     * @return 排行榜物件
     * @throws IOException 獲取失敗
     */
    public static Rank rank(int page) throws IOException {
        return rank(page, PixivRankMode.DAILY, PixivRankContent.OVERALL);
    }

    /**
     * 搜尋
     * @param keywords 關鍵字
     * @param page 頁碼
     * @param artworkType 搜尋作品類別
     * @param order 排序方式
     * @param mode 搜尋作品年齡分類
     * @param sMode 關鍵字搜尋方式
     * @param type 搜尋作品類別
     * @return 搜尋結果物件
     * @throws IOException 獲取失敗
     */
    public static SearchResult search(String keywords, int page, @NotNull PixivSearchArtworkType artworkType, PixivSearchOrder order, @NotNull PixivSearchMode mode, @NotNull PixivSearchStrictMode sMode, @NotNull PixivSearchType type) throws IOException {
        String encodedKeywords = URLEncoder.encode(keywords, "UTF-8").replace("+", "%20");

        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("ajax/search/" + artworkType + '/' + encodedKeywords);
        pub.addParameter("word", encodedKeywords);
        pub.addParameter("order", order);
        pub.addParameter("mode", mode);
        pub.addParameter("p", page);
        pub.addParameter("s_mode", sMode);
        pub.addParameter("type", type);
        String url = pub.build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String resultType;
        switch (artworkType) {
            case ARTWORKS:
                resultType = "illustManga";
                break;
            case ILLUSTRATIONS:
                resultType = "illust";
                break;
            case MANGA:
                resultType = "manga";
                break;
            case NOVELS: resultType = "novel";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + artworkType);
        }

        String json = Objects.requireNonNull(res.body()).string();

        res.close();

        return new SearchResult(json, resultType);
    }

    /**
     * 搜尋
     * @param keywords 關鍵字
     * @param page 頁碼
     * @return 搜尋結果物件
     * @throws IOException 獲取失敗
     */
    public static SearchResult search(String keywords, int page) throws IOException {
        return search(keywords, page, PixivSearchArtworkType.ILLUSTRATIONS, PixivSearchOrder.NEW_TO_OLD, PixivSearchMode.SAFE, PixivSearchStrictMode.S_TAG, PixivSearchType.ILLUST);
    }

    /**
     * 關注用戶
     * @param id 用戶ID
     * @return HTTP狀態碼
     * @throws IOException 讀取網路資料失敗
     */
    public static int followUser(int id) throws IOException {
        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("bookmark_add.php");
        String url = pub.build();

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

        int code = response.code();

        response.close();

        return code;
    }

    /**
     * 取消關注用戶
     * @param id 用戶ID
     * @return HTTP狀態碼
     * @throws IOException 讀取網路資料失敗
     */
    public static int unfollowUser(int id) throws IOException {
        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("rpc_group_setting.php");
        String url = pub.build();

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

        int code = response.code();

        response.close();
        return code;
    }

    /**
     * 獲取已關注用戶的新作品
     * @param artworkType 作品類型。只能為Illusts(插畫)或Novels(小說)，若是填入Manga(漫畫)則會拋出IllegalArgumentException。
     * @param searchMode 年齡限制
     * @param page 頁碼
     * @return 已關注用戶的新作品
     * @throws IOException 讀取網路資料失敗
     */
    public static FollowingLatestWork getFollowingLatestArtwork(PixivArtworkType artworkType, PixivSearchMode searchMode, int page) throws IOException {
        if (artworkType.equals(PixivArtworkType.MANGA)) {
            throw new IllegalArgumentException("The type must be Illusts or Novels");
        }
        if (searchMode.equals(PixivSearchMode.SAFE)) {
            throw new IllegalArgumentException("The mode must be SAFE or R18");
        }
        String type = artworkType.equals(PixivArtworkType.ILLUSTS) ? "illust" : "novel";
        String mode = searchMode.equals(PixivSearchMode.ALL) ? "all" : "r18";

        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("ajax/follow_latest/" + type);
        pub.addParameter("mode", mode);
        pub.addParameter("p", page);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(pub.build());

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

        return new FollowingLatestWork(data.getJSONObject("body"), artworkType.equals(PixivArtworkType.ILLUSTS) ? "illust" : "novel");
    }

    /**
     * 獲取MyPixiv的新作品
     * @param artworkType 作品類型
     * @param searchMode 年齡限制
     * @param page 頁碼
     * @return 已關注用戶的新作品
     * @throws IOException 讀取網路資料失敗
     */
    public static MyPixivLatestWork getMyPixivLatestArtwork(PixivArtworkType artworkType, PixivSearchMode searchMode, int page) throws IOException {
        if (artworkType.equals(PixivArtworkType.MANGA)) {
            throw new IllegalArgumentException("The type must be Illusts or Novels");
        }
        if (searchMode.equals(PixivSearchMode.SAFE)) {
            throw new IllegalArgumentException("The mode must be SAFE or R18");
        }
        String type = artworkType.equals(PixivArtworkType.ILLUSTS) ? "illust" : "novel";
        String mode = searchMode.equals(PixivSearchMode.ALL) ? "all" : "r18";

        PixivUrlBuilder pub = new PixivUrlBuilder();
        pub.setPath("aajax/mypixiv_latest/" + type);
        pub.addParameter("mode", mode);
        pub.addParameter("p", page);

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(pub.build());

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

        return new MyPixivLatestWork(data.getJSONObject("body"), artworkType.equals(PixivArtworkType.ILLUSTS) ? "illust" : "novel");
    }

    /**
     * 獲取首頁中與插畫相關的資訊
     * @return 相關資訊的JSON Object
     * @throws IOException 讀取網路資料失敗
     */
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
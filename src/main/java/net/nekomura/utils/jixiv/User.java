package net.nekomura.utils.jixiv;

import com.google.common.collect.Iterators;
import net.nekomura.utils.jixiv.Enums.artwork.PixivArtworkType;
import net.nekomura.utils.jixiv.Enums.bookmark.BookmarkRestrict;
import net.nekomura.utils.jixiv.Utils.SortUtils;
import net.nekomura.utils.jixiv.Utils.UserAgentUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class User {
    private final int id;
    private final JSONObject profile;
    private final JSONObject preloadData;
    private String phpSession;
    private String userAgent;

    public User(int id, JSONObject profile, JSONObject preloadData, String phpSession, String userAgent) {
        this.id = id;
        this.profile = profile;
        this.preloadData = preloadData;
        this.phpSession = phpSession;
        this.userAgent = userAgent;
    }

    private String userAgent() {
        if (userAgent == null || userAgent.isEmpty()) {
            return UserAgentUtils.random();
        }else {
            return userAgent;
        }
    }

    /**
     * 獲取使用者指定作品類型所有作品id
     * @param type 作品類型
     * @return 使用者指定作品類型所有作品id
     * @throws IOException 獲取失敗
     */
    public int[] getUserArtworks(@NotNull PixivArtworkType type) throws IOException {
        if (profile.getJSONObject("body").get(type.name().toLowerCase()) instanceof JSONObject) {
        int keySize = Iterators.size(profile.getJSONObject("body").getJSONObject(type.name().toLowerCase()).keys());
        int[] artworks = new int[keySize];

        for (int i = 0; i < keySize; i++) {
            artworks[i] = Integer.parseInt(Iterators.get(profile.getJSONObject("body").getJSONObject(type.name().toLowerCase()).keys(), i));
        }

        return SortUtils.reverseBubbleSort(artworks);
        }else {
            return new int[0];
        }
    }

    /**
     * 獲取使用者id
     * @return 使用者id
     */
    public int getId() {
        return id;
    }

    /**
     * 獲取使用者名稱
     * @return 使用者名稱
     */
    public String getName() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("name");
    }

    /**
     * 獲取使用者頭像之url
     * @return 使用者頭像之url
     */
    public String getAvatarSmallUrl() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("image");
    }

    /**
     * 獲取使用者大頭像之url
     * @return 使用者大頭像之url
     */
    public String getAvatarBigUrl() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("imageBig");
    }

    /**
     * 獲取使用者頭像
     * @return 使用者頭像
     * @throws IOException 獲取失敗
     */
    public byte[] getAvatarSmall() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(getAvatarSmallUrl());
        rb.addHeader("Referer", "https://www.pixiv.net/artworks");
        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();
        return Objects.requireNonNull(res.body()).bytes();
    }

    /**
     * 獲取使用者大頭像
     * @return 使用者大頭像
     * @throws IOException 獲取失敗
     */
    public byte[] getAvatarBig() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(getAvatarBigUrl());
        rb.addHeader("Referer", "https://www.pixiv.net/artworks");
        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();
        return Objects.requireNonNull(res.body()).bytes();
    }

    /**
     * 使用者是否為Pixiv Premium
     * @return 使用者是否為Pixiv Premium
     */
    public boolean isPremium() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getBoolean("premium");
    }

    /**
     * 是否已關注
     * @return 是否已關注
     */
    public boolean isFollowed() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getBoolean("isFollowed");
    }

    /**
     * 是否為我的pixiv
     * @return 是否為我的pixiv
     */
    public boolean isMyPixiv() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getBoolean("isMypixiv");
    }

    /**
     * 是否加入黑名單
     * @return 是否加入黑名單
     */
    public boolean isBlocking() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getBoolean("isBlocking");
    }

    /**
     * 獲取用戶頁背景圖片之url
     * @return 用戶頁背景圖片之url
     */
    public String getBackgroundUrl() {
        if (preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).get("background") != null) {
            return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getJSONObject("background").getString("url");
        }else {
            return null;
        }
    }

    /**
     * 獲取用戶頁背景圖片
     * @return 用戶頁背景圖片
     * @throws IOException 獲取失敗
     */
    public byte[] getBackground() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(getBackgroundUrl());
        rb.addHeader("Referer", "https://www.pixiv.net/artworks");
        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();
        return Objects.requireNonNull(res.body()).bytes();
    }

    /**
     * 獲取關注數
     * @return 關注數
     */
    public int getFollowingCount() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getInt("following");
    }

    /**
     * 是否互相關注
     * @return 是否互相關注
     */
    public boolean followedBack() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getBoolean("followedBack");
    }

    /**
     * 獲取自我介紹
     * @return 自我介紹
     */
    public String getComment() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("comment");
    }

    /**
     * 獲取自我介紹html文本
     * @return 自我介紹html文本
     */
    public String getCommentHtml() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("commentHtml");
    }

    /**
     * 獲取使用者主頁
     * @return 使用者主頁
     */
    public String getWebPage() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("webpage");
    }

    /**
     * 獲取使用者的Workspace
     * @return 使用者的Workspace
     */
    public String getWorkspace() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getString("workspace");
    }

    /**
     * 獲取使用者的群組
     * @return 使用者的群組
     */
    private JSONArray getGroup() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getJSONArray("group");
    }

    /**
     * 獲取使用者的群組id
     * @param index 加入的第幾個群組
     * @return 群組id
     */
    public int getGroupId(int index) {
        return getGroup().getJSONObject(index).getInt("id");
    }

    /**
     * 獲取使用者的群組名稱
     * @param index 加入的第幾個群組
     * @return 群組名稱
     */
    public String getGroupName(int index) {
        return getGroup().getJSONObject(index).getString("title");
    }

    /**
     * 獲取使用者的群組icon的url
     * @param index 加入的第幾個群組
     * @return 群組icon的url
     */
    public String getGroupIconUrl(int index) {
        return getGroup().getJSONObject(index).getString("iconUrl");
    }

    /**
     * 是否為官方帳號
     * @return 是否為官方帳號
     */
    public boolean isOfficial() {
        return preloadData.getJSONObject("user").getJSONObject(String.valueOf(id)).getBoolean("official");
    }

    /**
     * 獲取用戶所有公開收藏
     * @param page 頁數
     * @return 此用戶的收藏第指定頁數
     * @throws IOException
     */
    public Bookmark getBookmarkList(int page) throws IOException {
        return getBookmarkList(page, BookmarkRestrict.SHOW, null);
    }

    /**
     * 獲取用戶收藏
     * @param page 頁數
     * @param restrict 收藏限制。分為公開和非公開兩種。非公開僅限自己的收藏，其餘會顯示
     * @return 此用戶的收藏第指定頁數
     * @throws IOException
     */
    public Bookmark getBookmarkList(int page, BookmarkRestrict restrict) throws IOException {
        return getBookmarkList(page, restrict, null);
    }

    /**
     * 獲取用戶收藏
     * @param page 頁數
     * @param restrict 收藏限制。分為公開和非公開兩種。非公開僅限自己的收藏，其餘會顯示
     * @param tag 收藏的標籤名稱
     * @return 此用戶的收藏第指定頁數
     * @throws IOException
     */
    public Bookmark getBookmarkList(int page, BookmarkRestrict restrict, String tag) throws IOException {
        if (page < 0) {
            throw new IllegalArgumentException("The arg 'page' must be a natural number.");
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder();
        if (tag == null || tag.isEmpty()) {
            rb = rb.url("https://www.pixiv.net/ajax/user/" + this.getId() +"/illusts/bookmarks?tag=&offset=" + (48 * (page - 1)) + "&limit=48&rest=" + restrict.toString().toLowerCase());
        }else {
            rb = rb.url("https://www.pixiv.net/ajax/user/" + this.getId() +"/illusts/bookmarks?tag=" + tag +"&offset=" + (48 * (page - 1)) + "&limit=48&rest=" + restrict.toString().toLowerCase());
        }

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + phpSession);
        rb.addHeader("user-agent", userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String json = Objects.requireNonNull(res.body()).string();

        return new Bookmark(page, new JSONObject(json));
    }
}
package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.enums.artwork.PixivArtworkType;
import net.nekomura.utils.jixiv.enums.bookmark.BookmarkRestrict;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private final int id;
    private final JSONObject profile;
    private final JSONObject data;

    public User(int id, JSONObject profile, JSONObject data) {
        this.id = id;
        this.profile = profile;
        this.data = data;
    }

    /**
     * 獲取使用者指定作品類型所有作品id
     * @param type 作品類型
     * @return 使用者指定作品類型所有作品id
     */
    public int[] getUserArtworks(@NotNull PixivArtworkType type) {
        if (profile.getJSONObject("body").get(type.name().toLowerCase()) instanceof JSONObject) {
            Iterator<String> keys = profile.getJSONObject("body").getJSONObject(type.name().toLowerCase()).keys();
            int[] artworks = new int[0];
            while (keys.hasNext()) {
                artworks = Arrays.copyOf(artworks, artworks.length + 1);
                artworks[artworks.length - 1] = Integer.parseInt(keys.next());
            }

            artworks = Arrays.stream(artworks).boxed().sorted(Collections.reverseOrder()).mapToInt(Integer::intValue).toArray();
            return artworks;
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
        return data.getJSONObject("body").getString("name");
    }

    /**
     * 獲取使用者頭像之url
     * @return 使用者頭像之url
     */
    public String getAvatarUrl() {
        return data.getJSONObject("body").getString("image");
    }

    /**
     * 獲取使用者大頭像之url
     * @return 使用者大頭像之url
     */
    public String getAvatarBigUrl() {
        return data.getJSONObject("body").getString("imageBig");
    }

    /**
     * 獲取使用者頭像
     * @return 使用者頭像
     * @throws IOException 獲取失敗
     */
    public byte[] getAvatar() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(getAvatarUrl());
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
        return data.getJSONObject("body").getBoolean("premium");
    }

    /**
     * 是否已關注
     * @return 是否已關注
     */
    public boolean isFollowed() {
        return data.getJSONObject("body").getBoolean("isFollowed");
    }

    /**
     * 是否為我的pixiv
     * @return 是否為My pixiv
     */
    public boolean isMyPixiv() {
        return data.getJSONObject("body").getBoolean("isMypixiv");
    }

    /**
     * 是否加入黑名單
     * @return 是否加入黑名單
     */
    public boolean isBlocking() {
        return data.getJSONObject("body").getBoolean("isBlocking");
    }

    /**
     * 獲取用戶頁背景圖片之url
     * @return 用戶頁背景圖片之url
     */
    public String getBackgroundUrl() {
        if (data.getJSONObject("body").isNull("background")) {
            return null;
        }else {
            return data.getJSONObject("body").getJSONObject("background").getString("url");
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
        return data.getJSONObject("body").getInt("following");
    }

    /**
     * 是否互相關注
     * @return 是否互相關注
     */
    public boolean isFollowedBack() {
        return data.getJSONObject("body").getBoolean("followedBack");
    }

    /**
     * 獲取自我介紹
     * @return 自我介紹
     */
    public String getComment() {
        return data.getJSONObject("body").getString("comment");
    }

    /**
     * 獲取自我介紹html文本
     * @return 自我介紹html文本
     */
    public String getCommentHtml() {
        return data.getJSONObject("body").getString("commentHtml");
    }

    /**
     * 獲取使用者主頁
     * @return 使用者主頁
     */
    public String getWebPage() {
        if (data.getJSONObject("body").isNull("webpage")) {
            return null;
        }
        return data.getJSONObject("body").getString("webpage");
    }

    /**
     * 獲取使用者的Workspace
     * @return 使用者的Workspace
     */
    @Nullable
    private JSONObject getWorkspace() {
        if (data.getJSONObject("body").isNull("workspace")) {
            return null;
        }
        return data.getJSONObject("body").getJSONObject("workspace");
    }

    /**
     * 獲取使用者的工作環境的電腦系統
     * @return 使用者的工作環境的電腦系統
     */
    public String getWorkspacePc() {
        JSONObject workspace = getWorkspace();
        if (workspace == null) {
            return null;
        }
        return workspace.getString("userWorkspacePc");
    }

    /**
     * 獲取使用者的工作環境的常用軟體
     * @return 使用者的工作環境的常用軟體
     */
    public String getWorkspaceTool() {
        JSONObject workspace = getWorkspace();
        if (workspace == null) {
            return null;
        }
        return workspace.getString("userWorkspaceTool");
    }

    /**
     * 獲取使用者的工作環境的數位板
     * @return 使用者的工作環境的數位板
     */
    public String getWorkspaceTablet() {
        JSONObject workspace = getWorkspace();
        if (workspace == null) {
            return null;
        }
        return workspace.getString("userWorkspaceTablet");
    }

    /**
     * 獲取使用者的工作環境的印表機
     * @return 使用者的工作環境的印表機
     */
    public String getWorkspacePrinter() {
        JSONObject workspace = getWorkspace();
        if (workspace == null) {
            return null;
        }
        return workspace.getString("userWorkspacePrinter");
    }

    /**
     * 獲取使用者的工作環境的桌面物品
     * @return 使用者的工作環境的桌面物品
     */
    public String getWorkspaceDesktop() {
        JSONObject workspace = getWorkspace();
        if (workspace == null) {
            return null;
        }
        return workspace.getString("userWorkspaceDesktop");
    }

    /**
     * 獲取使用者繪圖時聽的音樂
     * @return 使用者繪圖時聽的音樂
     */
    public String getWorkspaceMusic() {
        JSONObject workspace = getWorkspace();
        if (workspace == null) {
            return null;
        }
        return workspace.getString("userWorkspaceMusic");
    }

    /**
     * 獲取使用者的工作環境的桌子
     * @return 使用者的工作環境的桌子
     */
    public String getWorkspaceDesk() {
        JSONObject workspace = getWorkspace();
        if (workspace == null) {
            return null;
        }
        return workspace.getString("userWorkspaceDesk");
    }

    /**
     * 獲取使用者的工作環境的椅子
     * @return 使用者的工作環境的椅子
     */
    public String getWorkspaceChair() {
        JSONObject workspace = getWorkspace();
        if (workspace == null) {
            return null;
        }
        return workspace.getString("userWorkspaceChair");
    }

    /**
     * 獲取使用者的群組
     * @return 使用者的群組
     */
    private JSONArray getGroup() {
        return data.getJSONObject("body").getJSONArray("group");
    }

    /**
     * 獲取使用者的群組量
     * @return 使用者的群組量
     */
    public int getGroupCount() {
        if (getGroup() == null) {
            return 0;
        }
        return getGroup().length();
    }

    /**
     * 獲取使用者的群組id
     * @param index 加入的第幾個群組
     * @return 群組id
     */
    public String getGroupId(int index) {
        if (getGroup() == null) {
            return null;
        }
        return getGroup().getJSONObject(index).getString("id");
    }

    /**
     * 獲取使用者的群組名稱
     * @param index 加入的第幾個群組
     * @return 群組名稱
     */
    public String getGroupName(int index) {
        if (getGroup() == null) {
            return null;
        }
        return getGroup().getJSONObject(index).getString("title");
    }

    /**
     * 獲取使用者的群組icon的url
     * @param index 加入的第幾個群組
     * @return 群組icon的url
     */
    public String getGroupIconUrl(int index) {
        if (getGroup() == null) {
            return null;
        }
        return getGroup().getJSONObject(index).getString("iconUrl");
    }

    @Nullable
    private JSONObject getSocial() {
        if (data.getJSONObject("body").get("social") instanceof JSONArray) {
            return null;
        }
        return data.getJSONObject("body").getJSONObject("social");
    }

    /**
     * 獲取使用者的Twitter
     * @return 使用者的Twitter
     */
    public String getTwitter() {
        if (getSocial() == null || !getSocial().has("twitter")) {
            return null;
        }
        return getSocial().getJSONObject("twitter").getString("url");
    }

    /**
     * 獲取使用者的Tumblr
     * @return 使用者的Tumblr
     */
    public String getTumblr() {
        if (getSocial() == null || !getSocial().has("tumblr")) {
            return null;
        }
        return getSocial().getJSONObject("tumblr").getString("url");
    }

    /**
     * 獲取使用者的Pawoo
     * @return 使用者的Pawoo
     */
    public String getPawoo() {
        if (getSocial() == null || !getSocial().has("pawoo")) {
            return null;
        }
        return getSocial().getJSONObject("pawoo").getString("url");
    }

    /**
     * 獲取使用者的CircleMs
     * @return 使用者的CircleMs
     */
    public String getCircleMs() {
        if (getSocial() == null || !getSocial().has("circlems")) {
            return null;
        }
        return getSocial().getJSONObject("circlems").getString("url");
    }

    /**
     * 獲取使用者地區
     * @return 使用者的地區
     */
    public String getRegion() {
        return data.getJSONObject("body").getJSONObject("region").getString("name");
    }

    /**
     * 獲取使用者生日
     * @return 使用者的生日
     */
    public String getBirthday() {
        return data.getJSONObject("body").getJSONObject("birthDay").getString("name");
    }

    /**
     * 獲取使用者性別
     * @return 使用者的性別
     */
    public String getGender() {
        return data.getJSONObject("body").getJSONObject("gender").getString("name");
    }

    /**
     * 獲取使用者職業
     * @return 使用者的職業
     */
    public String getJob() {
        return data.getJSONObject("body").getJSONObject("job").getString("name");
    }

    /**
     * 是否為官方帳號
     * @return 是否為官方帳號
     */
    public boolean isOfficial() {
        return data.getJSONObject("body").getBoolean("official");
    }

    /**
     * 獲取用戶所有公開收藏
     * @param page 頁數 (從1算起)
     * @return 此用戶的收藏第指定頁數
     * @throws IOException 網路錯誤
     */
    public Bookmark getBookmarkList(int page) throws IOException {
        return getBookmarkList(page, BookmarkRestrict.SHOW, null);
    }

    /**
     * 獲取用戶收藏
     * @param page 頁數 (從1算起)
     * @param restrict 收藏限制。分為公開和非公開兩種。非公開僅限自己的收藏，其餘會顯示
     * @return 此用戶的收藏第指定頁數
     * @throws IOException 網路錯誤
     */
    public Bookmark getBookmarkList(int page, BookmarkRestrict restrict) throws IOException {
        return getBookmarkList(page, restrict, null);
    }

    /**
     * 獲取用戶收藏
     * @param page 頁數 (從1算起)
     * @param restrict 收藏限制。分為公開和非公開兩種。非公開僅限自己的收藏，其餘會顯示
     * @param tag 收藏的標籤名稱
     * @return 此用戶的收藏第指定頁數
     * @throws IOException 網路錯誤
     */
    public Bookmark getBookmarkList(int page, BookmarkRestrict restrict, String tag) throws IOException {
        return getBookmarkList(48 * (page - 1), 48, restrict, tag);
    }

    public Bookmark getBookmarkList(int offset, int limit) throws IOException {
        return getBookmarkList(offset, limit, BookmarkRestrict.SHOW, null);
    }

    public Bookmark getBookmarkList(int offset, int limit, BookmarkRestrict restrict) throws IOException {
        return getBookmarkList(offset, limit, restrict, null);
    }

    public Bookmark getBookmarkList(int offset, int limit, BookmarkRestrict restrict, String tag) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder();
        if (tag == null || tag.isEmpty()) {
            rb = rb.url("https://www.pixiv.net/ajax/user/" + this.getId() +"/illusts/bookmarks?tag=&offset=" + offset + "&limit=" + limit + "&rest=" + restrict.toString().toLowerCase());
        }else {
            rb = rb.url("https://www.pixiv.net/ajax/user/" + this.getId() +"/illusts/bookmarks?tag=" + tag +"&offset=" + offset + "&limit=" + limit + "&rest=" + restrict.toString().toLowerCase());
        }

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        String json = Objects.requireNonNull(res.body()).string();

        return new Bookmark(new JSONObject(json));
    }
}
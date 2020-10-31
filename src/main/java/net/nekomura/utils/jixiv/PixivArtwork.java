package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.Enums.PixivArtworkType;
import net.nekomura.utils.jixiv.Utils.UserAgentUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

    public PixivArtworkInfo get(int id) throws Exception {
        return new PixivArtworkInfo(id, getArtworkPreloadData(id));
    }

    @NotNull
    @Contract(pure = true)
    @Deprecated
    private String getArtworkType() throws Exception {
        if (this instanceof PixivIllustration) {
            return "illust";
        }else if (this instanceof PixivNovel) {
            return "novel";
        }else {
            throw new Exception("The variable must be a PixivImage or a net.nekomura.utils.jixiv.PixivNovel.");
        }
    }

    @Deprecated
    public String getTitle(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject(getArtworkType()).getJSONObject(String.valueOf(id)).getString("title");
    }

    @Deprecated
    public String getDescription(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject(getArtworkType()).getJSONObject(String.valueOf(id)).getString("description");
    }

    @Deprecated
    public String getRawDescription(int id) throws Exception {
        return Jsoup.parse(StringEscapeUtils.unescapeHtml4(getDescription(id)).replaceAll("(?i)<br[^>]*>", "br2n")).text().replace("br2n", "\r\n");
    }

    @Deprecated
    public String[] getTags(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        JSONArray tagsJsonArray = json.getJSONObject(getArtworkType()).getJSONObject(String.valueOf(id)).getJSONObject("tags").getJSONArray("tags");

        String[] tags = new String[tagsJsonArray.length()];
        for (int i = 0; i < tagsJsonArray.length(); i++) {
            tags[i] = tagsJsonArray.getJSONObject(i).getString("tag");
        }

        return tags;
    }

    @Deprecated
    public int getPageCount(int id) throws Exception {
        return getArtworkPreloadData(id).getJSONObject(getArtworkType()).getJSONObject(String.valueOf(id)).getInt("pageCount");
    }

    @Deprecated
    public int getViewCount(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject(getArtworkType()).getJSONObject(String.valueOf(id)).getInt("viewCount");
    }

    @Deprecated
    public int getBookmarkCount(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject(getArtworkType()).getJSONObject(String.valueOf(id)).getInt("bookmarkCount");
    }

    @Deprecated
    public int getCommentCount(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject(getArtworkType()).getJSONObject(String.valueOf(id)).getInt("commentCount");
    }

    @Deprecated
    public int getLikeCount(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject(getArtworkType()).getJSONObject(String.valueOf(id)).getInt("likeCount");
    }

    @Deprecated
    public int getImageResponseCount(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject(getArtworkType()).getJSONObject(String.valueOf(id)).getInt("imageResponseCount");
    }

    @Deprecated
    public int getAuthorID(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return Integer.parseInt(json.getJSONObject(getArtworkType()).getJSONObject(String.valueOf(id)).getString("userId"));
    }

    @Deprecated
    public String getCreateDate(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject(getArtworkType()).getJSONObject(String.valueOf(id)).getString("createDate");
    }

    @Deprecated
    public Calendar getCreateDateCalendar(int id) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        Date date = sdf.parse(getCreateDate(id));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("JST"));
        return calendar;
    }

    @Deprecated
    public String getUploadDate(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject(getArtworkType()).getJSONObject(String.valueOf(id)).getString("uploadDate");
    }

    @Deprecated
    public Calendar getUploadDateCalendar(int id) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        Date date = sdf.parse(getUploadDate(id));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("JST"));
        return calendar;
    }

    @Deprecated
    public String getAuthorName(int id) throws Exception {
        JSONObject json = getArtworkPreloadData(id);
        return json.getJSONObject(getArtworkType()).getJSONObject(String.valueOf(id)).getString("userName");
    }

    @Deprecated
    public boolean isNSFW(int id) throws Exception {
        return Arrays.asList(getTags(id)).contains("R-18") || Arrays.asList(getTags(id)).contains("R-18G");
    }

    @Deprecated
    public boolean isR18(int id) throws Exception {
        return Arrays.asList(getTags(id)).contains("R-18");
    }

    @Deprecated
    public boolean isR18G(int id) throws Exception {
        return Arrays.asList(getTags(id)).contains("R-18G");
    }

}

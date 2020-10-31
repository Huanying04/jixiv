package net.nekomura.utils.jixiv;

import com.google.common.collect.Iterators;
import net.nekomura.utils.jixiv.Enums.PixivArtworkType;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PixivArtworkInfo {
    private final int id;
    private final JSONObject preloadData;

    public PixivArtworkInfo(int id, JSONObject preloadData) {
        this.id = id;
        this.preloadData = preloadData;
    }

    /**
     * 獲取作品id
     * @return 作品id
     */
    public int getId() {
        return id;
    }

    public JSONObject getPreloadData() {
        return preloadData;
    }

    public PixivIllustrationInfo toIllustrationInfo() {
        if (Iterators.contains(preloadData.keys(), "illust"))
            return new PixivIllustrationInfo(id, preloadData);
        else
            throw new IllegalArgumentException("This is not a ");
    }

    public PixivNovelInfo toNovelInfo() {
        if (Iterators.contains(preloadData.keys(), "novel"))
            return new PixivNovelInfo(id, preloadData);
        else
            throw new IllegalArgumentException("This is not a ");
    }

    @NotNull
    @Contract(pure = true)
    private String artworkType() throws IllegalArgumentException {
        if (this instanceof PixivIllustrationInfo) {
            return "illust";
        }else if (this instanceof PixivNovelInfo) {
            return "novel";
        }else {
            throw new IllegalArgumentException("The variable must be a PixivImage or a net.nekomura.utils.jixiv.PixivNovel.");
        }
    }

    /**
     * 獲取作品標題
     * @return 作品標題
     */
    public String getTitle() {
        return preloadData.getJSONObject(artworkType()).getJSONObject(String.valueOf(id)).getString("title");
    }

    /**
     * 獲取作品說明
     * @return 作品說明
     */
    public String getDescription() {
        return preloadData.getJSONObject(artworkType()).getJSONObject(String.valueOf(id)).getString("description");
    }

    /**
     * 獲取作品說明明文
     * @return 作品說明明文
     */
    public String getRawDescription() {
        return Jsoup.parse(StringEscapeUtils.unescapeHtml4(getDescription()).replaceAll("(?i)<br[^>]*>", "br2n")).text().replace("br2n", "\r\n");
    }

    /**
     * 獲取作品所有標籤之原文名字
     * @return 作品所有標籤之原文名字
     */
    public String[] getTags() {
        JSONArray tagsJsonArray = preloadData.getJSONObject(artworkType()).getJSONObject(String.valueOf(id)).getJSONObject("tags").getJSONArray("tags");

        String[] tags = new String[tagsJsonArray.length()];
        for (int i = 0; i < tagsJsonArray.length(); i++) {
            tags[i] = tagsJsonArray.getJSONObject(i).getString("tag");
        }

        return tags;
    }

    /**
     * 獲取作品總頁數
     * @return 作品總頁數
     */
    public int getPageCount() {
        return preloadData.getJSONObject(artworkType()).getJSONObject(String.valueOf(id)).getInt("pageCount");
    }

    /**
     * 獲取作品閱覽次數
     * @return 作品閱覽次數
     */
    public int getViewCount() {
        return preloadData.getJSONObject(artworkType()).getJSONObject(String.valueOf(id)).getInt("viewCount");
    }

    /**
     * 獲取作品收藏次數
     * @return 作品收藏次數
     */
    public int getBookmarkCount() {
        return preloadData.getJSONObject(artworkType()).getJSONObject(String.valueOf(id)).getInt("bookmarkCount");
    }

    /**
     * 獲取作品評論數
     * @return 作品評論數
     */
    public int getCommentCount() {
        return preloadData.getJSONObject(artworkType()).getJSONObject(String.valueOf(id)).getInt("commentCount");
    }

    /**
     * 獲取作品讚數
     * @return 作品讚數
     */
    public int getLikeCount() {
        return preloadData.getJSONObject(artworkType()).getJSONObject(String.valueOf(id)).getInt("likeCount");
    }

    /**
     * 獲取作品Image Response Count
     * @return 作品Image Response Count
     */
    public int getImageResponseCount() {
        return preloadData.getJSONObject(artworkType()).getJSONObject(String.valueOf(id)).getInt("imageResponseCount");
    }

    /**
     * 獲取作品作者id
     * @return 作者id
     */
    public int getAuthorID() {
        return Integer.parseInt(preloadData.getJSONObject(artworkType()).getJSONObject(String.valueOf(id)).getString("userId"));
    }

    /**
     * 獲取作品作者用戶名稱
     * @return 作者用戶名稱
     */
    public String getAuthorName() {
        return preloadData.getJSONObject(artworkType()).getJSONObject(String.valueOf(id)).getString("userName");
    }

    /**
     * 獲取作品創建日期
     * @return 作品創建日期
     */
    public String getCreateDate() {
        return preloadData.getJSONObject(artworkType()).getJSONObject(String.valueOf(id)).getString("createDate");
    }

    /**
     * 獲取作品創建日期
     * @return 作品創建日期
     * @throws ParseException 解讀日期錯誤
     */
    public Calendar getCreateDateCalendar() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        Date date = sdf.parse(getCreateDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("JST"));
        return calendar;
    }

    /**
     * 獲取作品上傳日期
     * @return 作品上傳日期
     */
    public String getUploadDate() {
        return preloadData.getJSONObject(artworkType()).getJSONObject(String.valueOf(id)).getString("uploadDate");
    }

    /**
     * 獲取作品上傳日期
     * @return 作品上傳日期
     * @throws ParseException 解讀日期錯誤
     */
    public Calendar getUploadDateCalendar() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        Date date = sdf.parse(getUploadDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("JST"));
        return calendar;
    }

    /**
     * 作品是否為成人限制，即是否為R18或R18-G
     * @return 作品是否為R18或R18-G
     */
    public boolean isNSFW() {
        return Arrays.asList(getTags()).contains("R-18") || Arrays.asList(getTags()).contains("R-18G");
    }

    /**
     * 作品是否為R18
     * @return 作品是否為R18
     */
    public boolean isR18() {
        return Arrays.asList(getTags()).contains("R-18");
    }

    /**
     * 作品是否為R18-G
     * @return 作品是否為R18-G
     */
    public boolean isR18G() {
        return Arrays.asList(getTags()).contains("R-18G");
    }

}
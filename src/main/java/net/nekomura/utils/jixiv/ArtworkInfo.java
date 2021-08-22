package net.nekomura.utils.jixiv;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ArtworkInfo {
    private final int id;
    private final JSONObject data;

    public ArtworkInfo(int id, JSONObject data) {
        this.id = id;
        this.data = data;
    }

    /**
     * 獲取作品id
     * @return 作品id
     */
    public int getId() {
        return id;
    }

    public JSONObject getData() {
        return data;
    }

    @Deprecated
    public IllustrationInfo toIllustrationInfo() {
        AtomicBoolean b = new AtomicBoolean(false);
        Iterator<String> it = data.getJSONObject("body").keys();
        it.forEachRemaining((x) -> {
            if (x.equals("illustId")) {
                b.set(true);
            }
        });
        if (b.get()) {
            return new IllustrationInfo(id, data.getJSONObject("body"));
        }else {
            throw new IllegalArgumentException("The variable is not an IllustrationInfo");
        }
    }

    @Deprecated
    public NovelInfo toNovelInfo() {
        AtomicBoolean b = new AtomicBoolean(false);
        Iterator<String> it = data.getJSONObject("body").keys();
        it.forEachRemaining((x) -> {
            if (x.equals("content")) {
                b.set(true);
            }
        });
        if (b.get()) {
            return new NovelInfo(id, data.getJSONObject("body"));
        }else {
            throw new IllegalArgumentException("The variable is not a NovelInfo");
        }
    }

    /**
     * 獲取作品標題
     * @return 作品標題
     */
    public String getTitle() {
        return data.getString("title");
    }

    /**
     * 獲取作品說明
     * @return 作品說明
     */
    public String getDescription() {
        return data.getString("description");
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
        JSONArray tagsJsonArray = data.getJSONObject("tags").getJSONArray("tags");

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
        return data.getInt("pageCount");
    }

    /**
     * 獲取作品閱覽次數
     * @return 作品閱覽次數
     */
    public int getViewCount() {
        return data.getInt("viewCount");
    }

    /**
     * 獲取作品收藏次數
     * @return 作品收藏次數
     */
    public int getBookmarkCount() {
        return data.getInt("bookmarkCount");
    }

    /**
     * 獲取作品評論數
     * @return 作品評論數
     */
    public int getCommentCount() {
        return data.getInt("commentCount");
    }

    /**
     * 獲取作品讚數
     * @return 作品讚數
     */
    public int getLikeCount() {
        return data.getInt("likeCount");
    }

    /**
     * 獲取作品Image Response Count
     * @return 作品Image Response Count
     */
    public int getImageResponseCount() {
        return data.getInt("imageResponseCount");
    }

    /**
     * 獲取作品作者id
     * @return 作者id
     */
    public int getAuthorID() {
        return Integer.parseInt(data.getString("userId"));
    }

    /**
     * 獲取作品作者用戶名稱
     * @return 作者用戶名稱
     */
    public String getAuthorName() {
        return data.getString("userName");
    }

    /**
     * 獲取作品創建日期
     * @return 作品創建日期
     */
    public String getCreateDate() {
        return data.getString("createDate");
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
        return data.getString("uploadDate");
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
        return data.getInt("xRestrict") > 0;
    }

    /**
     * 作品是否為R18
     * @return 作品是否為R18
     */
    public boolean isR18() {
        return data.getInt("xRestrict") == 1;
    }

    /**
     * 作品是否為R18-G
     * @return 作品是否為R18-G
     */
    public boolean isR18G() {
        return data.getInt("xRestrict") == 2;
    }

    /**
     * 是否喜歡該作品
     * @return 是否喜歡該作品
     */
    public boolean isLiked() {
        return data.getBoolean("likeData");
    }

    /**
     * 是否收藏過該作品
     * @return 是否收藏過該作品
     */
    public boolean isBookmarked() {
        return !data.isNull("bookmarkData");
    }

    /**
     * 獲取收藏id
     * @return 收藏id
     */
    public long getBookmarkId() {
        if (!isBookmarked()) {
            throw new NullPointerException("你尚未收藏過這個作品");
        }

        return Long.parseLong(data.getJSONObject("bookmarkData").getString("id"));
    }

    /**
     * 該作品的收藏是否為私人
     * @return 該作品的收藏是否為私人
     */
    public boolean isBookmarkPrivate() {
        if (!isBookmarked()) {
            throw new NullPointerException("你尚未收藏過這個作品");
        }

        return data.getJSONObject("bookmarkData").getBoolean("private");
    }

}
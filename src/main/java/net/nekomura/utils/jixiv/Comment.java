package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.enums.artwork.PixivArtworkType;
import net.nekomura.utils.jixiv.exception.PixivException;
import net.nekomura.utils.jixiv.utils.PixivUrlBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Comment {
    JSONObject comment;
    private final PixivArtworkType type;

    public Comment(JSONObject data, PixivArtworkType type) {
        this.comment = data;
        this.type = type;
    }

    /**
     * 獲取發布評論的用戶的ID
     * @return 發布評論的用戶的ID
     */
    public int getUserId() {
        return Integer.parseInt(comment.getString("userId"));
    }

    /**
     * 獲取發布評論的用戶的暱稱
     * @return 發布評論的用戶的暱稱
     */
    public String getUserName() {
        return comment.getString("userName");
    }

    /**
     * 獲取發布評論的用戶的頭像URL
     * @return 發布評論的用戶的頭像URL
     */
    public String getAvatarImageUrl() {
        return comment.getString("img");
    }

    /**
     * 獲取評論的ID
     * @return 評論ID
     */
    public int getCommentId() {
        return Integer.parseInt(comment.getString("id"));
    }

    /**
     * 獲取評論內文
     * @return 評論內文
     */
    public String getComment() {
        return comment.getString("comment");
    }

    /**
     * 獲取表情貼圖的ID
     * @return 表情貼圖ID
     */
    public int getStampId() {
        if (comment.get("stampId") == null) {
            throw new PixivException("This is not a stamp comment");
        }
        return Integer.parseInt(comment.getString("stampId"));
    }

    /**
     * 獲取表情貼圖的連結。但通常為null。
     * 作品的URL為"https://s.pximg.net/common/images/stamp/generated-stamps/" + {@link #getStampId()} + "_s.jpg"
     * @return 表情貼圖的link
     */
    public String getStampLink() {
        return comment.getString("stampLink");
    }

    /**
     * 獲取評論的時間
     * @return 評論時間
     */
    public String getCommentDate() {
        return comment.getString("commentDate");
    }

    /**
     * 獲取評論的時間
     * @return 評論時間
     * @throws ParseException 時間解析失敗
     */
    public Calendar getCommentDateCalendar() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        Date date = sdf.parse(getCommentDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("JST"));
        return calendar;
    }

    /**
     * 可否編輯
     * @return 可否編輯
     */
    public boolean isEditable() {
        return comment.getBoolean("editable");
    }

    /**
     * 有無回信
     * @return 有無回信
     */
    public boolean hasReplies() {
        return comment.getBoolean("hasReplies");
    }

    /**
     * 獲取回信
     * @param page 頁碼。從1開始。
     * @return 第幾頁的所有回信
     * @throws IOException 獲取網路資料失敗
     */
    public Replies getReplies(int page) throws IOException {
        List<Reply> replies = new ArrayList<>();
        String url;
        if (type.equals(PixivArtworkType.ILLUSTS)) {
            PixivUrlBuilder pub = new PixivUrlBuilder();
            pub.setPath("ajax/illusts/comments/replies");
            pub.addParameter("comment_id", "" + this.getCommentId());
            pub.addParameter("page", "" + page);
            url = pub.build();
        }else if (type.equals(PixivArtworkType.NOVELS)) {
            PixivUrlBuilder pub = new PixivUrlBuilder();
            pub.setPath("ajax/novels/comments/replies");
            pub.addParameter("comment_id", "" + this.getCommentId());
            pub.addParameter("page", "" + page);
            url = pub.build();
        }else {
            throw new IllegalArgumentException();
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder rb = new Request.Builder().url(url);

        rb.addHeader("Referer", "https://www.pixiv.net");
        rb.addHeader("cookie", "PHPSESSID=" + Jixiv.PHPSESSID);
        rb.addHeader("user-agent", Jixiv.userAgent());

        rb.method("GET", null);

        Response res = okHttpClient.newCall(rb.build()).execute();

        JSONObject replyJson = new JSONObject(Objects.requireNonNull(res.body()).string());

        if (replyJson.getBoolean("error")) {
            throw new PixivException(replyJson.getString("message"));
        }

        for (int i = 0; i < replyJson.getJSONObject("body").getJSONArray("comments").length(); i++) {
            replies.add(new Reply(replyJson.getJSONObject("body").getJSONArray("comments").getJSONObject(i), type));
        }

        return new Replies(replies, replyJson.getJSONObject("body").getBoolean("hasNext"));
    }
}

package net.nekomura.utils.jixiv;

import net.nekomura.utils.jixiv.enums.artwork.PixivArtworkType;
import net.nekomura.utils.jixiv.exception.PixivException;
import net.nekomura.utils.jixiv.utils.UserAgentUtils;
import org.json.JSONObject;

public class Reply extends Comment{
    public Reply(JSONObject reply, PixivArtworkType type, String phpSession, String userAgent) {
        super(reply, type, phpSession, userAgent);
    }

    private String userAgent() {
        if (userAgent == null || userAgent.isEmpty()) {
            return UserAgentUtils.random();
        }else {
            return userAgent;
        }
    }

    /**
     * 獲取回信的根評論
     * @return 回信的根評論
     */
    public int getCommentRootId() {
        if (this.comment.get("commentRootId") == null) {
            throw new PixivException("The comment have no commentRootId");
        }
        return Integer.parseInt(this.comment.getString("commentRootId"));
    }

    /**
     * 獲取回信的母評論
     * @return 回信的母評論
     */
    public int getCommentParentId() {
        if (this.comment.get("commentParentId") == null) {
            throw new PixivException("The comment have no commentParentId");
        }
        return Integer.parseInt(this.comment.getString("commentParentId"));
    }

    /**
     * 獲取被回信的用戶的ID
     * @return 被回信的用戶的ID
     */
    public int getReplyToUserId() {
        if (this.comment.get("replyToUserId") == null) {
            throw new PixivException("replyToUserId");
        }
        return Integer.parseInt(this.comment.getString("replyToUserId"));
    }

    /**
     * 獲取被回信的用戶的名稱，但通常為null。
     * @return 被回信的用戶的名稱
     */
    public String getReplyToUserName() {
        return this.comment.getString("replyToUserName");
    }
}
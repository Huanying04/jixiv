package net.nekomura.utils.jixiv;

import java.util.List;

public class Replies {
    List<Reply> replies;
    private final boolean hasNext;

    public Replies(List<Reply> replies, boolean hasNext) {
        this.replies = replies;
        this.hasNext = hasNext;
    }

    /**
     * 獲取評論
     * @param index 該頁第幾個評論
     * @return 該頁第幾個評論物件
     */
    public Reply getReply(int index) {
        return replies.get(index);
    }

    /**
     * 有無下一頁
     * @return 有無下一頁
     */
    public boolean hasNext() {
        return this.hasNext;
    }
}

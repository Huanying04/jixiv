package net.nekomura.utils.jixiv;

import java.util.List;

public class Comments {
    private final List<Comment> comments;
    private final boolean hasNext;

    public Comments(List<Comment> comments, boolean hasNext) {
        this.comments = comments;
        this.hasNext = hasNext;
    }

    public Comment getComment(int index) {
        return comments.get(index);
    }

    public boolean hasNext() {
        return this.hasNext;
    }
}

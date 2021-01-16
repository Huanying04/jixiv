package net.nekomura.utils.jixiv.exception;

public class PixivException extends RuntimeException {
    public PixivException() {
        super();
    }

    public PixivException(String message) {
        super(message);
    }

    public PixivException(String message, Throwable cause) {
        super(message, cause);
    }

    public PixivException(Throwable cause) {
        super(cause);
    }
}

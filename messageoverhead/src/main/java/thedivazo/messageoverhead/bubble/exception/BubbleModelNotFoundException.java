package thedivazo.messageoverhead.bubble.exception;

public class BubbleModelNotFoundException extends Exception {
    public BubbleModelNotFoundException(String message) {
        super(message);
    }

    public BubbleModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BubbleModelNotFoundException(Throwable cause) {
        super(cause);
    }

    public BubbleModelNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

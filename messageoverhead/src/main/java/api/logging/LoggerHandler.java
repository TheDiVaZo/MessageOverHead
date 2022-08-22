package api.logging;

public interface LoggerHandler {

    void debug(Object message, Throwable throwable, Object... placeholders);

    void info(Object message, Throwable throwable, Object... placeholders);

    void warn(Object message, Throwable throwable, Object... placeholders);

    void error(Object message, Throwable throwable, Object... placeholders);

    default String replace(Object message, Object values){
        return String.format(message.toString(), values);
    }
}

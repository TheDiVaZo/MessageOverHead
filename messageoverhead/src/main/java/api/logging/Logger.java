package api.logging;

public final class Logger {

    private static LoggerHandler handler;

    private Logger(){}

    public static void init(LoggerHandler h){
        handler = h;
    }

    public static void debug(Object message, Object... placeholders){
        debug(message, null, placeholders);
    }

    public static void debug(Object message, Throwable throwable, Object... placeholders){
        handler.debug(message, throwable, placeholders);
    }

    public static void info(Object message, Object... placeholders){
        info(message, null, placeholders);
    }

    public static void info(Object message, Throwable throwable, Object... placeholders){
        handler.info(message, throwable, placeholders);
    }

    public static void warn(Object message, Object... placeholders){
        warn(message, null, placeholders);
    }

    public static void warn(Object message, Throwable throwable, Object... placeholders){
        handler.warn(message, throwable, placeholders);
    }

    public static void error(Object message, Object... placeholders){
        error(message, null, placeholders);
    }

    public static void error(Object message, Throwable throwable, Object... placeholders){
        handler.error(message, throwable, placeholders);
    }

}

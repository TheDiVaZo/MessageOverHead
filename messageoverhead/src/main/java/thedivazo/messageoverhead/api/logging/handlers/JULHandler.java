package thedivazo.messageoverhead.api.logging.handlers;


import thedivazo.messageoverhead.api.logging.LoggerHandler;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JULHandler implements LoggerHandler {
    private final Logger logger;

    public JULHandler(Logger logger) {
        this.logger = logger;
    }

    public Logger logger() {
        return logger;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (JULHandler) obj;
        return Objects.equals(this.logger, that.logger);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logger);
    }

    @Override
    public String toString() {
        return "JULHandler[" +
                "logger=" + logger + ']';
    }

    @Override
    public void debug(Object message, Throwable throwable, Object... placeholders) {
        logger.log(Level.INFO, message.toString(), placeholders);
        if (throwable != null) throwable.printStackTrace();

    }

    @Override
    public void info(Object message, Throwable throwable, Object... placeholders) {
        logger.log(Level.INFO, message.toString(), placeholders);
        if (throwable != null) throwable.printStackTrace();
    }

    @Override
    public void warn(Object message, Throwable throwable, Object... placeholders) {
        logger.log(Level.WARNING, message.toString(), placeholders);
        if (throwable != null) throwable.printStackTrace();
    }

    @Override
    public void error(Object message, Throwable throwable, Object... placeholders) {
        logger.log(Level.SEVERE, message.toString(), placeholders);
        if (throwable != null) throwable.printStackTrace();
    }


}

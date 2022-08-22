package api.logging.handlers;


import api.logging.LoggerHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public record JULHandler(Logger logger) implements LoggerHandler {

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

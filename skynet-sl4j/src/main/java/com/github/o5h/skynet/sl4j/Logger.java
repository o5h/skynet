package com.github.o5h.skynet.sl4j;

import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

public class Logger extends MarkerIgnoringBase {
    private static final long serialVersionUID = -517220405410904473L;

    public static final Logger SKY_NET_LOGGER = new Logger();

    public static final String DEBUG_LEVEL = "DEBUG";
    public static final String INFO_LEVEL = "INFO ";
    public static final String WARN_LEVEL = "WARN ";
    public static final String ERROR_LEVEL = "ERROR";

    protected Logger() {
    }

    public String getName() {
        return "Logger";
    }

    public final boolean isTraceEnabled() {
        return false;
    }

    public final void trace(String msg) {
    }

    public final void trace(String format, Object arg) {
    }

    public final void trace(String format, Object arg1, Object arg2) {
    }

    public final void trace(String format, Object... argArray) {
    }

    public final void trace(String msg, Throwable t) {
    }

    public final boolean isDebugEnabled() {
        return true;
    }

    public final void debug(String msg) {
        if (!isDebugEnabled()) {
            return;
        }
        log(DEBUG_LEVEL, msg);
    }

    public final void debug(String format, Object arg) {
        if (!isDebugEnabled()) {
            return;
        }
        log(DEBUG_LEVEL, MessageFormatter.format(format, arg).getMessage());
    }

    public final void debug(String format, Object arg1, Object arg2) {
        if (!isDebugEnabled()) {
            return;
        }
        log(DEBUG_LEVEL, MessageFormatter.format(format, arg1, arg2).getMessage());
    }

    public final void debug(String format, Object... argArray) {
        if (!isDebugEnabled()) {
            return;
        }
        log(DEBUG_LEVEL, MessageFormatter.arrayFormat(format, argArray).getMessage());
    }

    public final void debug(String msg, Throwable t) {
        if (!isDebugEnabled()) {
            return;
        }
        log(DEBUG_LEVEL, MessageFormatter.arrayFormat(msg, null, t).getMessage());
    }

    public final boolean isInfoEnabled() {
        return true;
    }

    public final void info(String msg) {
        if (!isInfoEnabled()) {
            return;
        }
        log(INFO_LEVEL, msg);
    }

    public final void info(String format, Object arg1) {
        if (!isInfoEnabled()) {
            return;
        }
        log(INFO_LEVEL, MessageFormatter.format(format, arg1).getMessage());
    }

    public final void info(String format, Object arg1, Object arg2) {
        if (!isInfoEnabled()) {
            return;
        }
        log(INFO_LEVEL, MessageFormatter.format(format, arg1, arg2).getMessage());
    }

    public final void info(String format, Object... argArray) {
        if (!isInfoEnabled()) {
            return;
        }
        log(INFO_LEVEL, MessageFormatter.arrayFormat(format, argArray).getMessage());
    }

    public final void info(String msg, Throwable t) {
        if (!isInfoEnabled()) {
            return;
        }
        log(INFO_LEVEL, MessageFormatter.arrayFormat(msg, null, t).getMessage());
    }

    public final boolean isWarnEnabled() {
        return true;
    }

    public final void warn(String msg) {
        if (!isWarnEnabled()) {
            return;
        }
        log(WARN_LEVEL, msg);
    }

    public final void warn(String format, Object arg1) {
        if (!isWarnEnabled()) {
            return;
        }
        log(WARN_LEVEL, MessageFormatter.format(format, arg1).getMessage());
    }

    public final void warn(String format, Object arg1, Object arg2) {
        if (!isWarnEnabled()) {
            return;
        }
        log(WARN_LEVEL, MessageFormatter.format(format, arg1, arg2).getMessage());
    }

    public final void warn(String format, Object... argArray) {
        if (!isWarnEnabled()) {
            return;
        }
        log(WARN_LEVEL, MessageFormatter.arrayFormat(format, argArray).getMessage());
    }

    public final void warn(String msg, Throwable t) {
        if (!isWarnEnabled()) {
            return;
        }
        log(WARN_LEVEL, MessageFormatter.arrayFormat(msg, new Object[]{}, t).getMessage());
    }

    public final boolean isErrorEnabled() {
        return true;
    }

    public final void error(String msg) {
        if (!isErrorEnabled()) {
            return;
        }
        log(ERROR_LEVEL, msg);
    }

    public final void error(String format, Object arg1) {
        if (!isErrorEnabled()) {
            return;
        }
        log(ERROR_LEVEL, MessageFormatter.format(format, arg1).getMessage());
    }

    public final void error(String format, Object arg1, Object arg2) {
        if (!isErrorEnabled()) {
            return;
        }
        log(ERROR_LEVEL, MessageFormatter.format(format, arg1, arg2).getMessage());
    }

    public final void error(String format, Object... argArray) {
        if (!isErrorEnabled()) {
            return;
        }
        log(ERROR_LEVEL, MessageFormatter.arrayFormat(format, argArray).getMessage());
    }

    public final void error(String msg, Throwable t) {
        if (!isErrorEnabled()) {
            return;
        }
        log(ERROR_LEVEL, MessageFormatter.arrayFormat(msg, null, t).getMessage());
    }

    private void log(String level, String msg) {
        System.out.println(level + ":" + msg);
    }

}

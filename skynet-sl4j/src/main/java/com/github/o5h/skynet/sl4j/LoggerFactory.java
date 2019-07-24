package com.github.o5h.skynet.sl4j;


import org.slf4j.ILoggerFactory;

public class LoggerFactory implements ILoggerFactory {
    public LoggerFactory() {
    }

    public org.slf4j.Logger getLogger(String name) {
        return Logger.SKY_NET_LOGGER;
    }
}
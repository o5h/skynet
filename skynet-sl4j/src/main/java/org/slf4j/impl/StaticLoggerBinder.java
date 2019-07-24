package org.slf4j.impl;

import com.github.o5h.skynet.sl4j.LoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder {
	private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
	public static String REQUESTED_API_VERSION = "1.6.99";
	private static final String loggerFactoryClassStr = LoggerFactory.class.getName();
	private final ILoggerFactory loggerFactory = new LoggerFactory();

	public static final StaticLoggerBinder getSingleton() {
		return SINGLETON;
	}

	private StaticLoggerBinder() {
	}

	public ILoggerFactory getLoggerFactory() {
		return this.loggerFactory;
	}

	public String getLoggerFactoryClassStr() {
		return loggerFactoryClassStr;
	}
}

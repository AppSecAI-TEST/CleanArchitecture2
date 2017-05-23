package com.cleanarchitecture.shishkin.base.database.querybuilder.sqlite;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class QueryBuildConfiguration {
	private static volatile QueryBuildConfiguration sInstance;

	private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
	private DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

	public static QueryBuildConfiguration getInstance() {
		if (sInstance == null) {
			synchronized (QueryBuildConfiguration.class) {
				if (sInstance == null) {
					sInstance = new QueryBuildConfiguration();
				}
			}
		}
		return sInstance;
	}

	private QueryBuildConfiguration() {
	}

	public DateTimeFormatter getDateFormat() {
		return dateFormat;
	}
	
	public DateTimeFormatter getDateTimeFormat() {
		return dateTimeFormat;
	}
	
	public void setDateFormat(String format) {
		setDateFormat(DateTimeFormat.forPattern(format));
	}
	
	public void setDateTimeFormat(String format) {
		setDateTimeFormat(DateTimeFormat.forPattern(format));
	}
	
	public void setDateFormat(DateTimeFormatter format) {
		dateFormat = format;
	}
	
	public void setDateTimeFormat(DateTimeFormatter format) {
		dateTimeFormat = format;
	}
}

package com.cleanarchitecture.shishkin.api.controller;

import com.cleanarchitecture.shishkin.BuildConfig;

public class Constant {

    public static final int BYTE = 1;
    public static final int KB = 1024;
    public static final int MB = 1048576;
    public static final int GB = 1073741824;

    public static final String ACTION_ADD_MESSAGE = BuildConfig.APPLICATION_ID + ".ACTION_ADD_MESSAGE";
    public static final String ACTION_ADD_DISTINCT_MESSAGE = BuildConfig.APPLICATION_ID + ".ACTION_ADD_DISTINCT_MESSAGE";
    public static final String ACTION_REPLACE_MESSAGE = BuildConfig.APPLICATION_ID + ".ACTION_REPLACE_MESSAGE";
    public static final String ACTION_REFRESH_MESSAGES = BuildConfig.APPLICATION_ID + ".ACTION_REFRESH_MESSAGES";
    public static final String ACTION_CLEAR_MESSAGES = BuildConfig.APPLICATION_ID + ".ACTION_CLEAR_MESSAGES";
    public static final String ACTION_SET_MESSAGES_COUNT = BuildConfig.APPLICATION_ID + ".ACTION_SET_MESSAGES_COUNT";

    private Constant() {
    }
}

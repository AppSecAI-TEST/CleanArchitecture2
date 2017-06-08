package com.cleanarchitecture.shishkin.base.controller;

public class ThemeController extends AbstractSmallController<IThemeSubscriber> implements IThemeController<IThemeSubscriber> {
    public static final String NAME = "ThemeController";
    public static final String SUBSCRIBER_TYPE = "IThemeSubscriber";

    @Override
    public String getTheme() {
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return SUBSCRIBER_TYPE;
    }

}

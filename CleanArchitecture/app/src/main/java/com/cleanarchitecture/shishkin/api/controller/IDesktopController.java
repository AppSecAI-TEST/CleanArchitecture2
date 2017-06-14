package com.cleanarchitecture.shishkin.api.controller;

@SuppressWarnings("unused")
public interface IDesktopController extends IModule {

    int getLayoutId(String name, int defaultId);

    void setDesktop(String desktop);

    void getDesktop();
}

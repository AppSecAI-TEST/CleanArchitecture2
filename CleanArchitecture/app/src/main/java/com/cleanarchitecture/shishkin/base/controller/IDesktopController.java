package com.cleanarchitecture.shishkin.base.controller;

public interface IDesktopController<T> extends ISmallController<T> {

    String getDesktop();

    void setDesktop(String desktop);

    void registerDesktop(String desktop);


}

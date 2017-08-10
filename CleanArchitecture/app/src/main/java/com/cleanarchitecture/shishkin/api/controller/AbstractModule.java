package com.cleanarchitecture.shishkin.api.controller;

public abstract class AbstractModule implements IModule {

    @Override
    public void onUnRegisterModule() {
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

}

package com.cleanarchitecture.shishkin.api.controller;

public abstract class AbstractModule implements IModule {

    @Override
    public void onUnRegister() {
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

}

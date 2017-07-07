package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

public interface IApplicationController extends IModule {

    Context getApplicationContext();

    String getExternalCachePath();

    String getExternalApplicationPath();

    String[] getRequiredPermisions();
}

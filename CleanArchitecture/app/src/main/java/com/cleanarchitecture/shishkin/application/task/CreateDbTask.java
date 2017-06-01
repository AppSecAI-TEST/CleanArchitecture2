package com.cleanarchitecture.shishkin.application.task;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.database.CleanArchitectureDb;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.event.ui.ShowToastEvent;
import com.cleanarchitecture.shishkin.base.repository.Repository;
import com.cleanarchitecture.shishkin.base.task.AbstractAsyncTask;
import com.github.snowdream.android.util.Log;

public class CreateDbTask extends AbstractAsyncTask {
    @Override
    public void run() {
        //Controllers.getInstance().getRepository().getDbProvider().connect(CleanArchitectureDb.class, CleanArchitectureDb.NAME);
    }
}

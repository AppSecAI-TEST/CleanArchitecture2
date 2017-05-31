package com.cleanarchitecture.shishkin.application.task;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.database.CleanArchitectureDb;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.event.ui.ShowToastEvent;
import com.cleanarchitecture.shishkin.base.task.AbstractAsyncTask;
import com.github.snowdream.android.util.Log;

public class CreateDbTask extends AbstractAsyncTask {
    @Override
    public void run() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            try {
                final CleanArchitectureDb db = Room.databaseBuilder(context, CleanArchitectureDb.class, CleanArchitectureDb.NAME)
                        .build();
                if (db != null) {
                    ApplicationController.getInstance().setDb(db);
                    EventBusController.getInstance().post(new ShowToastEvent("Room Db '" + CleanArchitectureDb.NAME + "' connected"));
                }
            } catch (Exception e) {
                Log.e("CreateDbTask", e.getMessage());
            }
        }
    }
}

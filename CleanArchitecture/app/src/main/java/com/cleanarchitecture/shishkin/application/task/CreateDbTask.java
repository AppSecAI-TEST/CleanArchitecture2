package com.cleanarchitecture.shishkin.application.task;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.database.CleanArchitectureDb;
import com.cleanarchitecture.shishkin.base.task.AbstractAsyncTask;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;

public class CreateDbTask extends AbstractAsyncTask {
    @Override
    public void run() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            if (ApplicationUtils.existsDb(context, CleanArchitectureDb.NAME)) {
                final CleanArchitectureDb db = Room.databaseBuilder(context, CleanArchitectureDb.class, CleanArchitectureDb.NAME)
                        .build();
                if (db != null) {
                    ApplicationController.getInstance().setDb(db);
                }
            }
        }
    }
}

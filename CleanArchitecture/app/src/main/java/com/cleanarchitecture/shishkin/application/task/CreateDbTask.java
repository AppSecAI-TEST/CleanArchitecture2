package com.cleanarchitecture.shishkin.application.task;

import android.content.Context;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.database.CleanArchitectureDatabaseHelper;
import com.cleanarchitecture.shishkin.base.task.AbstractAsyncTask;

public class CreateDbTask extends AbstractAsyncTask {
    @Override
    public void run() {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            final CleanArchitectureDatabaseHelper dbHelper = new CleanArchitectureDatabaseHelper(context);
            if (!dbHelper.exists(context, CleanArchitectureDatabaseHelper.DATABASE_NAME)) {
                dbHelper.create(context);
            }
        }
    }
}

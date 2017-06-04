package com.cleanarchitecture.shishkin.application.task;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.database.CleanArchitectureDb;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.repository.IRepository;
import com.cleanarchitecture.shishkin.base.task.AbstractAsyncTask;

public class BackupDbTask extends AbstractAsyncTask {
    @Override
    public void run() {
        final IRepository repository = Controllers.getInstance().getRepository();
        if (repository != null) {
            repository.getDbProvider().backup(CleanArchitectureDb.NAME, ApplicationController.EXTERNAL_STORAGE_APPLICATION_PATH);
        }
    }
}

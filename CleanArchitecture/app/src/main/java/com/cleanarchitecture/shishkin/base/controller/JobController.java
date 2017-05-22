package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.evernote.android.job.JobManager;

/**
 * Контроллер, отвечающий за работой планировщика заданий приложения
 */
public class JobController extends AbstractController {
    private static final String NAME = "JobController";
    private static volatile JobController sInstance;
    private JobManager mJobManager;

    public static void instantiate() {
        if (sInstance == null) {
            synchronized (JobController.class) {
                if (sInstance == null) {
                    sInstance = new JobController();
                }
            }
        }
    }

    public static JobController getInstance() {
        instantiate();
        return sInstance;
    }

    private JobController() {
        mJobManager = JobManager.create(ApplicationController.getInstance());
        mJobManager.addJobCreator(new BaseJobCreator());
    }

    @Override
    public String getName() {
        return NAME;
    }
}

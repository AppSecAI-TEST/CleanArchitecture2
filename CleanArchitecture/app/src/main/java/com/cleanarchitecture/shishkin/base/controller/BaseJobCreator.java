package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.job.ClearDiskCacheJob;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class BaseJobCreator implements JobCreator {
    @Override
    public Job create(String tag) {
        switch (tag) {
            case ClearDiskCacheJob.NAME:
                return new ClearDiskCacheJob();
            default:
                return null;
        }
    }
}

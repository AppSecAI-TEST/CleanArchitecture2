package com.cleanarchitecture.shishkin.base.job;

import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.event.ClearDiskCacheEvent;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

public class ClearDiskCacheJob extends Job {
    public static final String NAME = "ClearDiskCacheJob";

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        EventController.getInstance().post(new ClearDiskCacheEvent());
        return Result.SUCCESS;
    }

    public static void schedule() {
        new JobRequest.Builder(NAME)
                .setExact(TimeUnit.SECONDS.toMillis(5))
                .build()
                .schedule();
    }
}

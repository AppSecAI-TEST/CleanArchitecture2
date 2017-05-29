package com.cleanarchitecture.shishkin.base.task;

import android.os.AsyncTask;

import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.event.OnAsyncTaskCanceledEvent;
import com.cleanarchitecture.shishkin.base.event.OnAsyncTaskFinishedEvent;
import com.github.snowdream.android.util.Log;

public abstract class AbstractAsyncTask extends AsyncTask<Void, Void, Void> implements Runnable  {

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (isCancelled()) {
            return null;
        }

        run();

        return null;
    }

    @Override
    public abstract void run();

}

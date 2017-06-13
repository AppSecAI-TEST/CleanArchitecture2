package com.cleanarchitecture.shishkin.common.task;

import android.os.AsyncTask;

public abstract class AbstractAsyncTask extends AsyncTask<Void, Void, Void> implements Runnable {

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

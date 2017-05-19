package com.cleanarchitecture.shishkin.base.task;

import android.os.AsyncTask;

import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.event.OnAsyncTaskCanceledEvent;
import com.cleanarchitecture.shishkin.base.event.OnAsyncTaskFinishedEvent;
import com.github.snowdream.android.util.Log;

public abstract class AbstractAsyncTask extends AsyncTask<Void, Void, Void> implements Runnable, IAsyncTask  {

    private static final String LOG_TAG = "AbstractAsyncTask:";

    private Void[] mParams;
    private String mId;

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (isCancelled()) {
            return null;
        }

        mParams = params;

        try {
            run();
        } catch (Exception e){
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        EventController.getInstance().post(new OnAsyncTaskCanceledEvent(getId()));
        super.onCancelled();
    }

    @Override
    protected void onCancelled(Void result) {
        EventController.getInstance().post(new OnAsyncTaskCanceledEvent(getId()));
        super.onCancelled(result);
    }

    @Override
    protected void onPostExecute(Void param) {
        EventController.getInstance().post(new OnAsyncTaskFinishedEvent(getId()));
        super.onPostExecute(param);
    }


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }


    @Override
    public abstract void run();

    public Void[] getParams() {
        return mParams;
    }

}

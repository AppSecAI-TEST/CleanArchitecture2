package com.cleanarchitecture.shishkin.base.database.livedata;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.snowdream.android.util.Log;

public abstract class AbstractCursorViewModel<T extends AbstractCursorLiveData>
        extends AndroidViewModel {

    private static final String NAME = "AbstractCursorViewModel";

    @NonNull
    protected final T mCursorLiveData;

    public AbstractCursorViewModel(@NonNull Application application) {
        super(application);

        mCursorLiveData = createCursorLiveData(application);
    }

    @NonNull
    protected abstract T createCursorLiveData(@NonNull Application application);

    @Override
    protected void onCleared() {
        final Cursor cursor = mCursorLiveData.getValue();
        if (cursor != null) {
            Log.d(NAME, "onCleared() cursor.close()");
            cursor.close();
        }
    }

}
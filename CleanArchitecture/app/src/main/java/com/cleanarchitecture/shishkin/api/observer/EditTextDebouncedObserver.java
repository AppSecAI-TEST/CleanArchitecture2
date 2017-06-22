package com.cleanarchitecture.shishkin.api.observer;

import android.support.annotation.NonNull;
import android.widget.EditText;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.ui.EditTextAfterTextChangedEvent;

public class EditTextDebouncedObserver extends AbstractDebouncedObserver {

    private int mId = -1;

    public EditTextDebouncedObserver(@NonNull final EditText edittext, final long delay, final int id) {
        super(new EditTextObservable(edittext), delay);

        mId = id;
    }

    @Override
    public void onRun(Object arg) {
        AdminUtils.postEvent(new EditTextAfterTextChangedEvent((String) arg, mId));
    }

}

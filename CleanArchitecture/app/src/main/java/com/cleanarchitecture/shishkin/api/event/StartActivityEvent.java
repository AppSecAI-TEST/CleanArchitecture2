package com.cleanarchitecture.shishkin.api.event;

import android.content.Intent;

/**
 * Событие - выполнить команду "start activity"
 */
public class StartActivityEvent extends AbstractEvent {

    private Intent mIntent;

    public <F> StartActivityEvent(final Intent intent) {
        mIntent = intent;
    }

    public Intent getIntent() {
        return mIntent;
    }
}

package com.cleanarchitecture.shishkin.api.event.ui;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

public class EditTextAfterTextChangedEvent extends AbstractEvent {
    private String mText;
    private int mId;

    public EditTextAfterTextChangedEvent(final String text, final int id) {
        mText = text;
        mId = id;
    }

    public String getText() {
        return mText;
    }

    public int getId() {
        return mId;
    }

}

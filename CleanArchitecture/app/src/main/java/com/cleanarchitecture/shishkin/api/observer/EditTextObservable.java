package com.cleanarchitecture.shishkin.api.observer;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Observable;

public class EditTextObservable extends Observable implements TextWatcher {
    private EditText mEditText;

    public EditTextObservable(@NonNull final EditText view) {
        mEditText = view;
        mEditText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(final CharSequence s, final int start,
                              final int before, final int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        this.setChanged();
        this.notifyObservers(mEditText.getText().toString());
    }

}

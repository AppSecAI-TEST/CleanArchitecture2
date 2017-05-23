package com.cleanarchitecture.shishkin.base.presenter;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.cleanarchitecture.shishkin.base.content.MultipleUrisChangePresenterObserver;
import com.cleanarchitecture.shishkin.base.controller.ISubscribeable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractContentProviderPresenter<M> extends AbstractSubscribeablePresenter<M> {
    private final List<Uri> mUris = new ArrayList<>();

    public AbstractContentProviderPresenter(final Uri uri) {
        super();

        if (uri != null) {
            mUris.add(uri);
        }
    }

    public AbstractContentProviderPresenter(final List<Uri> uris) {
        super();

        if (uris != null && !uris.isEmpty()) {
            mUris.addAll(uris);
        }
    }

    @Nullable
    @Override
    public ISubscribeable onCreateContentObserver() {
        return new MultipleUrisChangePresenterObserver(this, mUris);
    }

}

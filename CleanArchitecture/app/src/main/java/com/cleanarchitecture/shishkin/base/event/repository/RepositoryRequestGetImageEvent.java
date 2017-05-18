package com.cleanarchitecture.shishkin.base.event.repository;

import android.widget.ImageView;

import com.cleanarchitecture.shishkin.base.event.AbstractEvent;
import com.cleanarchitecture.shishkin.base.repository.net.requests.AbstractRequest;

import java.lang.ref.WeakReference;

public class RepositoryRequestGetImageEvent extends AbstractEvent {
    private int mRank = 0;
    private String mUrl;
    private WeakReference<ImageView> mView = null;
    private boolean mWithCache = true;
    private int mPlaceholder = -1;
    private int mErrorHolder = -1;

    public RepositoryRequestGetImageEvent(final int rank, final String url, final ImageView view, final boolean withCache, int placeholder, int errorholder) {
        this (rank, url, view, withCache);

        mPlaceholder = placeholder;
        mErrorHolder = errorholder;
    }

    public RepositoryRequestGetImageEvent(final String url, final ImageView view, final boolean withCache, int placeholder, int errorholder) {
        this (AbstractRequest.MIN_RANK, url, view, withCache);

        mPlaceholder = placeholder;
        mErrorHolder = errorholder;
    }

    public RepositoryRequestGetImageEvent(final int rank, final String url, final ImageView view, final boolean withCache) {
        mRank = rank;
        mUrl = url;
        mView = new WeakReference<ImageView>(view);
        mWithCache = withCache;
    }

    public RepositoryRequestGetImageEvent(final String url, final ImageView view) {
        this(AbstractRequest.MIN_RANK, url, view, true);
    }

    public RepositoryRequestGetImageEvent(final String url, final ImageView view, final boolean withCache) {
        this(AbstractRequest.MIN_RANK, url, view, withCache);
    }

    public int getRank() {
        return mRank;
    }

    public String getUrl() {
        return mUrl;
    }

    public WeakReference<ImageView> getView() {
        return mView;
    }

    public boolean isWithCache() {
        return mWithCache;
    }

    public int getPlaceHolder() {
        return mPlaceholder;
    }

    public int getErrorHolder() {
        return mErrorHolder;
    }

}

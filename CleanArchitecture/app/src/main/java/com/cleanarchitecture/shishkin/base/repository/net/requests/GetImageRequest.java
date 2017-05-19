package com.cleanarchitecture.shishkin.base.repository.net.requests;

import android.content.Context;
import android.widget.ImageView;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.event.repository.RepositoryRequestGetImageEvent;
import com.cleanarchitecture.shishkin.base.repository.NetProvider;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class GetImageRequest extends AbstractRequest {
    public static final String NAME = "ImageViewLoadRequest";

    private String mUrl;
    private ImageView mView = null;
    private boolean mWithCache = false;
    private int mPlaceholder = -1;
    private int mErrorHolder = -1;

    public GetImageRequest(final RepositoryRequestGetImageEvent event) {
        super(event.getRank());

        mUrl = event.getUrl();
        mView = event.getView().get();
        mWithCache = event.isWithCache();
        mErrorHolder = event.getErrorHolder();
        mPlaceholder = event.getPlaceHolder();
    }

    @Override
    public void run() {
        final Picasso picasso = NetProvider.getInstance().getPicasso();
        if (picasso == null) {
            return;
        }

        ApplicationUtils.runOnUiThread(() -> {
            if (!mWithCache) {
                final RequestCreator requestCreator = picasso.load(mUrl);
                if (mPlaceholder > 0) {
                    requestCreator.placeholder(mPlaceholder);
                }
                if (mErrorHolder > 0) {
                    requestCreator.error(mErrorHolder);
                }
                requestCreator.into(mView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                    }
                });
            } else {
                final RequestCreator requestCreator = picasso.load(mUrl);
                if (mPlaceholder > 0) {
                    requestCreator.placeholder(mPlaceholder);
                }
                if (mErrorHolder > 0) {
                    requestCreator.error(mErrorHolder);
                }
                requestCreator.networkPolicy(NetworkPolicy.OFFLINE)
                        .into(mView, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                //Try again online if cache failed
                                final RequestCreator requestCreator = picasso.load(mUrl);
                                if (mPlaceholder > 0) {
                                    requestCreator.placeholder(mPlaceholder);
                                }
                                if (mErrorHolder > 0) {
                                    requestCreator.error(mErrorHolder);
                                }
                                requestCreator.networkPolicy(NetworkPolicy.NO_CACHE)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                        .into(mView, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onError() {
                                            }
                                        });
                            }
                        });

            }
        });
    }
}

package com.cleanarchitecture.shishkin.base.ui.recyclerview;

import android.support.annotation.NonNull;
import android.view.View;

import com.cleanarchitecture.shishkin.base.ui.recyclerview.lifecycle.LifecycleCallbacks;

public abstract class AbstractLifecycleViewHolder extends AbstractViewHolder
        implements LifecycleCallbacks {

    /**
     * {@inheritDoc}
     */
    public AbstractLifecycleViewHolder(@NonNull final View itemView) {
        super(itemView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
    }

}
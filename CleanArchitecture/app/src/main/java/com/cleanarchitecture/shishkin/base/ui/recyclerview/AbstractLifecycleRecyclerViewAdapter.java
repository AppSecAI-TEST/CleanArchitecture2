package com.cleanarchitecture.shishkin.base.ui.recyclerview;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.base.ui.recyclerview.lifecycle.LifecycleCallbacks;
import com.cleanarchitecture.shishkin.base.ui.recyclerview.lifecycle.LifecycleCallbacksDispatcher;

public abstract class AbstractLifecycleRecyclerViewAdapter<E, VH extends AbstractViewHolder & LifecycleCallbacks>
        extends AbstractRecyclerViewAdapter<E, VH> implements LifecycleCallbacks {

    @NonNull
    private final LifecycleCallbacksDispatcher mLifecycleCallbacksDispatcher;

    public AbstractLifecycleRecyclerViewAdapter(@NonNull final Context context) {
        super(context);
        mLifecycleCallbacksDispatcher = new LifecycleCallbacksDispatcher();
    }

    /**
     * This method should be called when the activity or fragment is
     * being started.
     *
     * @see android.app.Activity#onStart
     * @see android.support.v4.app.Fragment#onStart
     */
    @Override
    public void onStart() {
        mLifecycleCallbacksDispatcher.dispatchStarted();
    }

    /**
     * This method should be called when the activity or fragment is
     * being resumed.
     *
     * @see android.app.Activity#onResume
     * @see android.support.v4.app.Fragment#onResume
     */
    @Override
    public void onResume() {
        mLifecycleCallbacksDispatcher.dispatchResumed();
    }

    /**
     * This method should be called when the activity or fragment is
     * being paused.
     *
     * @see android.app.Activity#onPause
     * @see android.support.v4.app.Fragment#onPause
     */
    @Override
    public void onPause() {
        mLifecycleCallbacksDispatcher.dispatchPaused();
    }

    /**
     * This method should be called when the activity or fragment is
     * being stopped.
     *
     * @see android.app.Activity#onStop
     * @see android.support.v4.app.Fragment#onStop
     */
    @Override
    public void onStop() {
        mLifecycleCallbacksDispatcher.dispatchStopped();
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, final int position) {
        super.onBindViewHolder(holder, position);
        mLifecycleCallbacksDispatcher.registerLifecycleCallbacks(holder);
    }

    @Override
    public void onViewRecycled(@NonNull final VH holder) {
        mLifecycleCallbacksDispatcher.unregisterLifecycleCallbacks(holder);
        super.onViewRecycled(holder);
    }

}
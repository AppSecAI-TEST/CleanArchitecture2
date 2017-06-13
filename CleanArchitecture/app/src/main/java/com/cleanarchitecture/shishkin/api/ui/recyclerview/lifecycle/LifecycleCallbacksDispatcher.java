package com.cleanarchitecture.shishkin.api.ui.recyclerview.lifecycle;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class LifecycleCallbacksDispatcher {

    private static final String TAG = "LifecycleDispatcher";

    private static final int NONE = 0;
    private static final int STARTED = 1;
    private static final int RESUMED = 1 << 1;

    @IntDef(value = {NONE, STARTED, RESUMED}, flag = true)
    private @interface LifecycleState {
    }

    @LifecycleState
    private int mLifecycleState = NONE;
    private Map<LifecycleCallbacks, Integer> mLifecycleCallbacks;

    public LifecycleCallbacksDispatcher() {
        mLifecycleCallbacks = new LinkedHashMap<>();
    }

    public void registerLifecycleCallbacks(@NonNull final LifecycleCallbacks lifecycleCallbacks) {
        Log.i(TAG, "#registerLifecycleCallbacks: " + lifecycleCallbacks);
        if (!mLifecycleCallbacks.containsKey(lifecycleCallbacks)) {
            @LifecycleState int lifecycleState = NONE;
            if ((mLifecycleState & STARTED) == STARTED) {
                lifecycleCallbacks.onStart();
                lifecycleState |= STARTED;
            }
            if ((mLifecycleState & RESUMED) == RESUMED) {
                lifecycleCallbacks.onResume();
                lifecycleState |= RESUMED;
            }
            mLifecycleCallbacks.put(lifecycleCallbacks, lifecycleState);
        }
    }

    public void unregisterLifecycleCallbacks(@NonNull final LifecycleCallbacks lifecycleCallbacks) {
        Log.i(TAG, "#unregisterLifecycleCallbacks: " + lifecycleCallbacks);
        if (mLifecycleCallbacks.containsKey(lifecycleCallbacks)) {
            @LifecycleState final int lifecycleState = mLifecycleCallbacks.remove(lifecycleCallbacks);
            if ((lifecycleState & RESUMED) == RESUMED) {
                lifecycleCallbacks.onPause();
            }
            if ((lifecycleState & STARTED) == STARTED) {
                lifecycleCallbacks.onStop();
            }
        }
    }

    public void dispatchStarted() {
        mLifecycleState |= STARTED;
        Log.i(TAG, "#dispatchStarted: state=" + mLifecycleState);

        for (final Map.Entry<LifecycleCallbacks, Integer> entry : mLifecycleCallbacks.entrySet()) {
            @LifecycleState final int lifecycleState = entry.getValue();
            if ((lifecycleState & STARTED) != STARTED) {
                final LifecycleCallbacks lifecycleCallbacks = entry.getKey();
                lifecycleCallbacks.onStart();
                entry.setValue(lifecycleState | STARTED);
            }
        }
    }

    public void dispatchResumed() {
        mLifecycleState |= RESUMED;
        Log.i(TAG, "#dispatchResumed: state=" + mLifecycleState);

        for (final Map.Entry<LifecycleCallbacks, Integer> entry : mLifecycleCallbacks.entrySet()) {
            @LifecycleState final int lifecycleState = entry.getValue();
            if ((lifecycleState & RESUMED) != RESUMED) {
                final LifecycleCallbacks lifecycleCallbacks = entry.getKey();
                lifecycleCallbacks.onResume();
                entry.setValue(lifecycleState | RESUMED);
            }
        }
    }

    public void dispatchPaused() {
        mLifecycleState &= ~RESUMED;
        Log.i(TAG, "#dispatchPaused: state=" + mLifecycleState);

        for (final Map.Entry<LifecycleCallbacks, Integer> entry : mLifecycleCallbacks.entrySet()) {
            @LifecycleState final int lifecycleState = entry.getValue();
            if ((lifecycleState & RESUMED) == RESUMED) {
                final LifecycleCallbacks lifecycleCallbacks = entry.getKey();
                lifecycleCallbacks.onPause();
                entry.setValue(lifecycleState & ~RESUMED);
            }
        }
    }

    public void dispatchStopped() {
        mLifecycleState &= ~STARTED;
        Log.i(TAG, "#dispatchStopped: state=" + mLifecycleState);

        for (final Map.Entry<LifecycleCallbacks, Integer> entry : mLifecycleCallbacks.entrySet()) {
            @LifecycleState final int lifecycleState = entry.getValue();
            if ((lifecycleState & STARTED) == STARTED) {
                final LifecycleCallbacks lifecycleCallbacks = entry.getKey();
                lifecycleCallbacks.onStop();
                entry.setValue(lifecycleState & ~STARTED);
            }
        }
    }

}

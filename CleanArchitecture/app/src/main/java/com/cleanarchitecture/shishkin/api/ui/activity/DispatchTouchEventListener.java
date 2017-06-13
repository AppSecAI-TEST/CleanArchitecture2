package com.cleanarchitecture.shishkin.api.ui.activity;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

/**
 * Interface indicates classes responsible for dispatching touch event.
 */
public interface DispatchTouchEventListener {

    /**
     * Called to process touch screen events. You can override this to
     * intercept all touch screen events before they are dispatched to the
     * window. Be sure to call this implementation for touch screen events
     * that should be handled normally.
     *
     * @param ev The touch screen event.
     * @return true if this event was consumed.
     */
    boolean dispatchTouchEvent(@NonNull final MotionEvent ev);

}

package com.cleanarchitecture.shishkin.base.ui.activity;

/**
 * Interface indicates classes responsible for handling back pressed event.
 */
public interface OnBackPressListener {

    /**
     * Call when the the user's press of the back key is detected.
     * Return true if back pressed event has been correctly handled by component, false otherwise.
     */
    boolean onBackPressed();

}
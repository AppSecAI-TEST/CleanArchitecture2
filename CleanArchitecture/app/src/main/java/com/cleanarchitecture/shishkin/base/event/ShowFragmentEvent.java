package com.cleanarchitecture.shishkin.base.event;

import android.support.v4.app.Fragment;

/**
 * Событие - выполнить команду "показать указанный фрагмент"
 */
public class ShowFragmentEvent extends AbstractEvent {
    private Fragment mFragment;

    private boolean mAllowingStateLoss = false;
    private boolean mAddToBackStack = true;
    private boolean mClearBackStack = false;
    private boolean mAnimate = true;

    public ShowFragmentEvent(final Fragment fragment) {
        mFragment = fragment;
    }

    public ShowFragmentEvent(final Fragment fragment, final boolean allowingStateLoss) {
        mFragment = fragment;
        mAllowingStateLoss = allowingStateLoss;
    }

    public ShowFragmentEvent(final Fragment fragment, final boolean allowingStateLoss, final boolean addToBackStack, final boolean clearBackStack, final boolean animate) {
        mFragment = fragment;
        mAllowingStateLoss = allowingStateLoss;
        mAddToBackStack = addToBackStack;
        mClearBackStack = clearBackStack;
        mAnimate = animate;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public boolean isAllowingStateLoss() {
        return mAllowingStateLoss;
    }

    public boolean isAddToBackStack() {
        return mAddToBackStack;
    }

    public boolean isClearBackStack() {
        return mClearBackStack;
    }

    public boolean isAnimate() {
        return mAnimate;
    }


}

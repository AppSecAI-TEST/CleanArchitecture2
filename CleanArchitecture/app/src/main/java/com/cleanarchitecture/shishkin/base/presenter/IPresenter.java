package com.cleanarchitecture.shishkin.base.presenter;

import com.cleanarchitecture.shishkin.base.lifecycle.ILifecycle;

public interface IPresenter<M> extends ILifecycle {

    String getName();

    void setModel(M model);

    M getModel();

    void updateView();

    boolean isRegister();

    boolean validate();

    /**
     * Show progress bar.
     */
    void showProgressBar();

    /**
     * Hide progress bar.
     */
    void hideProgressBar();


}

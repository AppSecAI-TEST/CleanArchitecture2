package com.cleanarchitecture.shishkin.base.ui.fragment;

import com.cleanarchitecture.shishkin.base.presenter.ContentFragmentPresenter;

public interface IContentFragment extends IFragment {
    ContentFragmentPresenter getContentFragmentPresenter();
}

package com.cleanarchitecture.shishkin.api.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.application.event.expandableboard.OnExpandableBoardClick;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

import net.cachapa.expandablelayout.ExpandableLayout;

public class ExpandableBoardPresenter extends AbstractPresenter {

    public static final String NAME = ExpandableBoardPresenter.class.getName();
    private static final String MESSAGE = "MESSAGE";

    private ExpandableLayout mBoardLayout;
    private View mBoardButton;
    private View mBoardRoot;
    private TextView mBoardTextView;

    public void bindView(@NonNull final View root) {
        mBoardLayout = ViewUtils.findView(root, R.id.expandableLayout);
        mBoardRoot = ViewUtils.findView(root, R.id.board_root);
        mBoardTextView = ViewUtils.findView(root, R.id.board);
        mBoardTextView.setOnClickListener(this::onClick);
        mBoardButton = ViewUtils.findView(root, R.id.board_button);
        mBoardButton.setOnClickListener(this::onClick);

        final Bundle bundle = AdminUtils.getStateData(NAME);
        if (bundle != null) {
            mBoardTextView.setText(bundle.getString(MESSAGE));
        }
    }

    @Override
    public boolean validate() {
        return (super.validate()
                && mBoardLayout != null
                && mBoardRoot != null
                && mBoardButton != null
                && mBoardTextView != null
        );
    }

    private void onClick(View view) {
        if (validate()) {
            switch (view.getId()) {
                case R.id.board_button:
                    mBoardLayout.expand();
                    break;

                case R.id.board:
                    AdminUtils.postEvent(new OnExpandableBoardClick());
                    mBoardLayout.collapse();
                    break;
            }
        }
    }

    public void setText(final String text) {
        if (validate()) {
            ApplicationUtils.runOnUiThread(
                    () -> {
                        if (StringUtils.isNullOrEmpty(text)) {
                            mBoardTextView.setText("");
                        } else {
                            mBoardTextView.setText(text);
                        }
                    }
            );
        }
    }

    public void hideBoard() {
        if (validate()) {
            ApplicationUtils.runOnUiThread(
                    () -> {
                        mBoardLayout.collapse();
                        mBoardRoot.setVisibility(View.GONE);
                    }
            );
        }
    }

    public void showBoard() {
        if (validate()) {
            ApplicationUtils.runOnUiThread(
                    () -> {
                        mBoardRoot.setVisibility(View.VISIBLE);
                    }
            );
        }
    }

    @Override
    public Bundle getStateData() {
        final Bundle bundle = new Bundle();
        bundle.putString(MESSAGE, mBoardTextView.getText().toString());
        return bundle;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isRegister() {
        return true;
    }
}

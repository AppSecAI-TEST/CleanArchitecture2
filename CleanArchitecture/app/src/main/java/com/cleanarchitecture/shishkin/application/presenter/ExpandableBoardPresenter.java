package com.cleanarchitecture.shishkin.application.presenter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.presenter.AbstractPresenter;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

public class ExpandableBoardPresenter extends AbstractPresenter {

    public static final String NAME = ExpandableBoardPresenter.class.getName();

    private ExpandableRelativeLayout mBoardLayout;
    private View mBoardButton;
    private TextView mBoardTextView;

    public void bindView(@NonNull final View root) {
        mBoardLayout = ViewUtils.findView(root, R.id.expandableLayout);
        mBoardTextView = ViewUtils.findView(root, R.id.board);
        mBoardTextView.setOnClickListener(this::onClick);
        mBoardButton = ViewUtils.findView(root, R.id.board_button);
        mBoardButton.setOnClickListener(this::onClick);
    }

    @Override
    public boolean validate() {
        return (super.validate()
                && mBoardLayout != null
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
                    mBoardLayout.collapse();
                    break;
            }
        }
    }

    public void setText(final String text) {
        if (validate()) {
            if (StringUtils.isNullOrEmpty(text)) {
                mBoardTextView.setText("");
            } else {
                mBoardTextView.setText(text);
            }
        }
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

package com.cleanarchitecture.shishkin.api.presenter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

import java.lang.ref.WeakReference;

@SuppressWarnings("unused")
public class SideMenuPresenter extends AbstractPresenter<Void> {
    public static final String NAME = SideMenuPresenter.class.getName();

    private WeakReference<Context> mContext;
    private WeakReference<View> mSideMenuLL;
    private WeakReference<LinearLayout> mHeader;
    private WeakReference<LinearLayout> mBody;
    private WeakReference<LinearLayout> mFooter;

    public void bindView(final View root, final Context context) {

        if (root == null || context == null) {
            return;
        }

        mContext = new WeakReference<>(context);

        final View sideMenuLL = ViewUtils.findView(root, R.id.sidemenu_ll);
        if (sideMenuLL != null) {
            mSideMenuLL = new WeakReference<>(sideMenuLL);
        }

        final LinearLayout header = ViewUtils.findView(root, R.id.sidemenu_header);
        if (header != null) {
            mHeader = new WeakReference<>(header);
        }

        final LinearLayout body = ViewUtils.findView(root, R.id.sidemenu_body);
        if (body != null) {
            mBody = new WeakReference<>(body);
        }

        final LinearLayout footer = ViewUtils.findView(root, R.id.sidemenu_footer);
        if (footer != null) {
            mFooter = new WeakReference<>(footer);
        }

        fillSideMenu();
    }

    @Override
    public void onDestroyLifecycle() {
        super.onDestroyLifecycle();

        mContext = null;
        mSideMenuLL = null;
    }

    @Override
    public boolean validate() {
        return (super.validate()
                && mContext != null && mContext.get() != null
                && mSideMenuLL != null && mSideMenuLL.get() != null
                && mHeader != null && mHeader.get() != null
                && mBody != null && mBody.get() != null
                && mFooter != null && mFooter.get() != null
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isRegister() {
        return false;
    }

    private void fillSideMenu() {
        if (validate()) {

        }
    }

}

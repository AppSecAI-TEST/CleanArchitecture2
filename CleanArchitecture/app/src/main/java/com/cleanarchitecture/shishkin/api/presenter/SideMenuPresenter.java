package com.cleanarchitecture.shishkin.api.presenter;

import android.view.View;
import android.widget.LinearLayout;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.DesktopController;
import com.cleanarchitecture.shishkin.api.controller.IDesktopController;
import com.cleanarchitecture.shishkin.api.controller.IDesktopSubscriber;
import com.cleanarchitecture.shishkin.api.event.ShowFragmentEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseFinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.ui.fragment.AbstractContentFragment;
import com.cleanarchitecture.shishkin.api.ui.fragment.SettingApplicationFragment;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

import java.lang.ref.WeakReference;

@SuppressWarnings("unused")
public class SideMenuPresenter extends AbstractPresenter<Void> implements View.OnClickListener {
    public static final String NAME = SideMenuPresenter.class.getName();

    private WeakReference<View> mSideMenuLL;
    private WeakReference<LinearLayout> mHeader;
    private WeakReference<LinearLayout> mBody;
    private WeakReference<LinearLayout> mFooter;

    public void bindView(final View root) {

        if (root == null) {
            return;
        }

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

        ViewUtils.findView(root, R.id.exit).setOnClickListener(this);
        ViewUtils.findView(root, R.id.desktop).setOnClickListener(this);
        ViewUtils.findView(root, R.id.desktop_order).setOnClickListener(this);
        ViewUtils.findView(root, R.id.setting).setOnClickListener(this);
        ViewUtils.findView(root, R.id.log_view).setOnClickListener(this);

        final AbstractContentFragment fragment = AdminUtils.getContentFragment();
        if (fragment != null && fragment instanceof IDesktopSubscriber) {
            ViewUtils.findView(root, R.id.desktop_order).setEnabled(true);
        } else {
            ViewUtils.findView(root, R.id.desktop_order).setEnabled(false);
        }
    }

    @Override
    public void onDestroyLifecycle() {
        super.onDestroyLifecycle();

        mSideMenuLL = null;
        mHeader = null;
        mBody = null;
        mFooter = null;
    }

    @Override
    public boolean validate() {
        return (super.validate()
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
        return true;
    }

    @Override
    public void onClick(View v) {
        final IDesktopController controller = Admin.getInstance().get(DesktopController.NAME);
        switch (v.getId()) {
            case R.id.exit:
                AdminUtils.postEvent(new UseCaseFinishApplicationEvent());
                break;

            case R.id.desktop:
                if (controller != null) {
                    controller.getDesktop();
                }
                break;

            case R.id.setting:
                AdminUtils.postEvent(new ShowFragmentEvent(SettingApplicationFragment.newInstance()));
                break;

            case R.id.desktop_order:
                if (controller != null) {
                    final AbstractContentFragment fragment = AdminUtils.getContentFragment();
                    if (fragment != null && fragment instanceof IDesktopSubscriber) {
                        controller.setDesktopOrder((IDesktopSubscriber) fragment);
                    }
                }
                break;

            case R.id.log_view:
                AdminUtils.viewLog();
                break;

        }
    }
}

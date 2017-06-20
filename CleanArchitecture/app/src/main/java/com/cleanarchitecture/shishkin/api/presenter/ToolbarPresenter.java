package com.cleanarchitecture.shishkin.api.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.INavigationController;
import com.cleanarchitecture.shishkin.api.controller.NavigationController;
import com.cleanarchitecture.shishkin.api.event.OnNetworkConnectedEvent;
import com.cleanarchitecture.shishkin.api.event.OnNetworkDisconnectedEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.OnToolbarClickEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.OnToolbarMenuItemClickEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarHideProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarInitEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarPrepareEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarResetEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetBackNavigationEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetBackgroundEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetItemEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetMenuEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetTitleEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarShowProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.api.ui.fragment.AbstractContentFragment;
import com.cleanarchitecture.shishkin.common.net.Connectivity;
import com.cleanarchitecture.shishkin.common.ui.widget.AutoResizeTextView;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

@SuppressWarnings("unused")
public class ToolbarPresenter extends AbstractPresenter<Void> implements IToolbarPresenter {
    public static final String NAME = ToolbarPresenter.class.getName();

    private WeakReference<Context> mContext;
    private WeakReference<View> mToolbarLL;
    private WeakReference<RelativeLayout> mToolbar;
    private WeakReference<AutoResizeTextView> mTitle;
    private WeakReference<MaterialProgressBar> mHorizontalPogressBar;
    private WeakReference<ImageView> mHome;
    private WeakReference<ImageView> mMenu;
    private WeakReference<ImageView> mItem;
    private WeakReference<AVLoadingIndicatorView> mPogressBar;

    private PopupMenu mPopupMenu;
    private boolean mPopupMenuShow = false;
    private int mMenuId = 0;
    private boolean mBackNavigation = false;
    private boolean mShow = true;

    public void bindView(final View root, final Context context) {

        if (root == null || context == null) {
            return;
        }

        final View toolbarLL = ViewUtils.findView(root, R.id.toolbar_ll);
        final RelativeLayout toolbar = ViewUtils.findView(root, R.id.toolbar);
        final AutoResizeTextView title = ViewUtils.findView(root, R.id.title);
        final ImageView home = ViewUtils.findView(root, R.id.back);
        final ImageView menu = ViewUtils.findView(root, R.id.menu);
        final ImageView item = ViewUtils.findView(root, R.id.item);
        final MaterialProgressBar horizontalProgresBar = ViewUtils.findView(root, R.id.horizontalprogressbar);
        final AVLoadingIndicatorView progresBar = ViewUtils.findView(root, R.id.presenterProgressBar);

        if (menu != null) {
            menu.setOnClickListener(this::onClick);
            mMenu = new WeakReference<>(menu);
        }
        if (item != null) {
            item.setOnClickListener(this::onClick);
            mItem = new WeakReference<>(item);
        }
        if (home != null) {
            home.setOnClickListener(this::onClick);
            mHome = new WeakReference<>(home);
        }
        mContext = new WeakReference<>(context);
        if (toolbarLL != null) {
            mToolbarLL = new WeakReference<>(toolbarLL);
        }
        if (toolbar != null) {
            mToolbar = new WeakReference<>(toolbar);
        }
        if (title != null) {
            mTitle = new WeakReference<>(title);
        }
        if (horizontalProgresBar != null) {
            mHorizontalPogressBar = new WeakReference<>(horizontalProgresBar);
        }
        if (progresBar != null) {
            mPogressBar = new WeakReference<>(progresBar);
        }

        checkNetStatus(context);
    }

    @Override
    public void onDestroyLifecycle() {
        super.onDestroyLifecycle();

        dismissMenu();
        mContext = null;
        mToolbarLL = null;
        mToolbar = null;
        mTitle = null;
        mHorizontalPogressBar = null;
        mPogressBar = null;
        mHome = null;
        mMenu = null;
        mItem = null;

        AdminUtils.removeStickyEvent(new ToolbarInitEvent());
    }

    @Override
    public boolean validate() {
        return (super.validate()
                && mContext != null && mContext.get() != null
                && mToolbarLL != null && mToolbarLL.get() != null
                && mToolbar != null && mToolbar.get() != null
                && mTitle != null && mTitle.get() != null
                && mHorizontalPogressBar != null && mHorizontalPogressBar.get() != null
                && mPogressBar != null && mPogressBar.get() != null
                && mHome != null && mHome.get() != null
                && mMenu != null && mMenu.get() != null
                && mItem != null && mItem.get() != null
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<String> hasSubscriberType() {
        final List<String> list = super.hasSubscriberType();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    @Override
    public boolean isRegister() {
        return true;
    }

    private void dismissMenu() {
        try {
            if (mPopupMenu != null) {
                mPopupMenu.dismiss();
                mPopupMenu = null;
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onResumeLifecycle() {
        super.onResumeLifecycle();

        AdminUtils.postStickyEvent(new ToolbarInitEvent());
    }

    private void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.item:
            case R.id.back:
                AdminUtils.postEvent(new OnToolbarClickEvent(view));
                break;

            case R.id.menu:
                if (mPopupMenuShow) {
                    mPopupMenu.dismiss();
                } else {
                    showPopupMenu(mMenu.get());
                }
                break;

        }
    }

    private void showPopupMenu(final View view) {
        try {
            mPopupMenuShow = true;
            final Context wrapper = new ContextThemeWrapper(mContext.get(), AdminUtils.getStyleId("PopupMenu", R.style.PopupMenu));
            mPopupMenu = new PopupMenu(wrapper, view);
            final Field mFieldPopup = mPopupMenu.getClass().getDeclaredField("mPopup");
            mFieldPopup.setAccessible(true);
            final MenuPopupHelper mPopup = (MenuPopupHelper) mFieldPopup.get(mPopupMenu);
            mPopup.setForceShowIcon(true);
            mPopupMenu.inflate(mMenuId);
            mPopupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
            mPopupMenu.setOnDismissListener(this::onDismiss);
            mPopupMenu.show();
        } catch (Exception e) {
        }
    }

    private boolean onMenuItemClick(MenuItem item) {
        AdminUtils.postEvent(new OnToolbarMenuItemClickEvent(item));
        return true;
    }

    private void onDismiss(final PopupMenu menu) {
        mPopupMenuShow = false;
        mPopupMenu = null;
    }

    @Override
    public boolean hasBackNavigation() {
        return mBackNavigation;
    }

    @Override
    public void setBackNavigation(final boolean backNavigation) {
        ApplicationUtils.runOnUiThread(() -> {
            if (validate()) {
                mBackNavigation = backNavigation;
                mHome.get().setVisibility(mBackNavigation ? View.VISIBLE : View.INVISIBLE);
                if (!mBackNavigation) {
                    mHome.get().setImageDrawable(ViewUtils.getDrawable(mContext.get(), R.mipmap.ic_menu));
                } else {
                    mHome.get().setImageDrawable(ViewUtils.getDrawable(mContext.get(), R.mipmap.ic_arrow_left_bold_circle_outline));
                }
            }
        });
    }

    @Override
    public void resetToolbar() {
        ApplicationUtils.runOnUiThread(() -> resetToolbarTask());
    }

    @Override
    public void setHome(final int iconId, final boolean isVisible) {
        ApplicationUtils.runOnUiThread(() -> {
            if (validate()) {
                mHome.get().setImageResource(iconId);
                mHome.get().setVisibility(isVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void resetToolbarTask() {
        setShow(true);
        setHome(0, false);
        setTitle(0, null);
        setMenu(0, false);
        setItem(0, false);
        setBackNavigation(false);

        AdminUtils.postEvent(new ToolbarPrepareEvent());
    }

    @Override
    public boolean isShow() {
        return mShow;
    }

    @Override
    public void setShow(boolean isShow) {
        mShow = isShow;

        ApplicationUtils.runOnUiThread(() -> {
            if (validate()) {
                if (isShow()) {
                    mToolbarLL.get().setVisibility(View.VISIBLE);
                } else {
                    mToolbarLL.get().setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void showHorizontalProgressBar() {
        ApplicationUtils.runOnUiThread(() -> {
            if (validate()) {
                final AbstractContentFragment fragment = AdminUtils.getContentFragment();
                if (fragment != null) {
                    final SwipeRefreshLayout swipeRefreshLayout = fragment.getSwipeRefreshLayout();
                    if (swipeRefreshLayout == null) {
                        mHorizontalPogressBar.get().setVisibility(View.VISIBLE);
                    } else if (!swipeRefreshLayout.isRefreshing()) {
                        mHorizontalPogressBar.get().setVisibility(View.VISIBLE);
                    }
                } else {
                    mHorizontalPogressBar.get().setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void hideHorizontalProgressBar() {
        ApplicationUtils.runOnUiThread(() -> {
            if (validate()) {
                hideSwipeRefreshLayout();
                mHorizontalPogressBar.get().setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void showProgressBar() {
        ApplicationUtils.runOnUiThread(() -> {
            if (validate()) {
                final AbstractContentFragment fragment = AdminUtils.getContentFragment();
                if (fragment != null) {
                    final SwipeRefreshLayout swipeRefreshLayout = fragment.getSwipeRefreshLayout();
                    if (swipeRefreshLayout == null) {
                        mPogressBar.get().setVisibility(View.VISIBLE);
                    } else if (!swipeRefreshLayout.isRefreshing()) {
                        mPogressBar.get().setVisibility(View.VISIBLE);
                    }
                } else {
                    mPogressBar.get().setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void hideProgressBar() {
        ApplicationUtils.runOnUiThread(() -> {
            if (validate()) {
                hideSwipeRefreshLayout();
                mPogressBar.get().setVisibility(View.GONE);
            }
        });
    }

    private void hideSwipeRefreshLayout() {
        final AbstractContentFragment fragment = AdminUtils.getContentFragment();
        if (fragment != null) {
            final SwipeRefreshLayout swipeRefreshLayout = fragment.getSwipeRefreshLayout();
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public void setItem(final int itemId, final boolean isVisible) {
        ApplicationUtils.runOnUiThread(() -> {
            if (validate()) {
                if (itemId > 0) {
                    mItem.get().setImageDrawable(ViewUtils.getDrawable(mContext.get(), itemId));
                }
                mItem.get().setVisibility(isVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void setBackground(final Drawable drawable) {
        ApplicationUtils.runOnUiThread(() -> {
            if (validate()) {
                ViewUtils.setBackground(mToolbar.get(), drawable);
            }
        });
    }

    @Override
    public void setMenu(final int menuId, final boolean isVisible) {
        ApplicationUtils.runOnUiThread(() -> {
            if (validate()) {
                mMenuId = menuId;
                mMenu.get().setVisibility(isVisible ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void setTitle(final int iconId, final String title) {
        ApplicationUtils.runOnUiThread(() -> {
            if (validate()) {
                if (StringUtils.isNullOrEmpty(title)) {
                    mTitle.get().setText(StringUtils.EMPTY);
                } else {
                    mTitle.get().setText(title);
                }
                mTitle.get().setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);
            }
        });
    }

    private void checkNetStatus(final Context context) {
        if (context != null) {
            if (Connectivity.isNetworkConnected(context)) {
                onNetworkConnected();
            } else {
                onNetworkDisconnected();
            }
        }
    }

    private void onNetworkConnected() {
        if (validate()) {
            setBackground(ViewUtils.getDrawable(mContext.get(), R.color.blue));
        }
    }

    private void onNetworkDisconnected() {
        if (validate()) {
            setBackground(ViewUtils.getDrawable(mContext.get(), R.color.orange));
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSetToolbarBackNavigationEvent(ToolbarSetBackNavigationEvent event) {
        setBackNavigation(event.getBackNavigation());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onResetToolbarEvent(ToolbarResetEvent event) {
        resetToolbar();
    }

    @Override
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSetToolbarTitleEvent(ToolbarSetTitleEvent event) {
        setTitle(event.getIconId(), event.getTitle());
    }

    @Override
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSetToolbarMenuEvent(ToolbarSetMenuEvent event) {
        setMenu(event.getMenuId(), event.isVisible());
    }

    @Override
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSetToolbarItemEvent(ToolbarSetItemEvent event) {
        setItem(event.getItemId(), event.isVisible());
    }

    @Override
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowHorizontalProgressBarEvent(ShowHorizontalProgressBarEvent event) {
        showHorizontalProgressBar();
    }

    @Override
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onHideHorizontalProgressBarEvent(HideHorizontalProgressBarEvent event) {
        hideHorizontalProgressBar();
    }

    @Override
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onToolbarShowProgressBarEvent(ToolbarShowProgressBarEvent event) {
        showProgressBar();
    }

    @Override
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onToolbarHideProgressBarEvent(ToolbarHideProgressBarEvent event) {
        hideProgressBar();
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToolbarOnClickEvent(OnToolbarClickEvent event) {
        final INavigationController controller = Admin.getInstance().get(NavigationController.NAME);
        if (controller != null) {
            final AbstractContentFragment fragment = controller.getContentFragment(AbstractContentFragment.class);
            if (fragment != null) {
                fragment.onClick(event.getView());
            }
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onToolbarSetBackgroundEvent(ToolbarSetBackgroundEvent event) {
        setBackground(event.getDrawable());
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkConnectedEvent(OnNetworkConnectedEvent event) {
        onNetworkConnected();
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkDisconnectedEvent(OnNetworkDisconnectedEvent event) {
        onNetworkDisconnected();
    }

}

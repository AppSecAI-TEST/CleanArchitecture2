package com.cleanarchitecture.shishkin.base.presenter;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.controller.LifecycleController;
import com.cleanarchitecture.shishkin.base.controller.NavigationController;
import com.cleanarchitecture.shishkin.base.event.toolbar.FragmentPrepareToolbarEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.OnToolbarClickEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.OnToolbarMenuItemClickEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarInitEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarResetEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetBackNavigationEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetItemEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetMenuEvent;
import com.cleanarchitecture.shishkin.base.event.toolbar.ToolbarSetTitleEvent;
import com.cleanarchitecture.shishkin.base.event.ui.HideHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractContentActivity;
import com.cleanarchitecture.shishkin.base.ui.fragment.AbstractContentFragment;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;
import com.cleanarchitecture.shishkin.base.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

@SuppressWarnings("unused")
public class ToolbarPresenter extends AbstractPresenter<Void> implements IToolbarPresenter {
    public static final String NAME = "ToolbarPresenter";

    private WeakReference<Context> mContext;
    private WeakReference<View> mToolbarLL;
    private WeakReference<TextView> mTitle;
    private WeakReference<MaterialProgressBar> mHorizontalPogressBar;
    private WeakReference<ImageView> mHome;
    private WeakReference<ImageView> mMenu;
    private WeakReference<ImageView> mItem;

    private PopupMenu mPopupMenu;
    private boolean mPopupMenuShow = false;
    private int mMenuId = 0;
    private boolean mBackNavigation = false;
    private boolean mShow = true;

    public void bindView(final View root, final Context context) {

        if (root == null || context == null) {
            return;
        }

        EventController.getInstance().register(this);

        final View toolbarLL = ViewUtils.findView(root, R.id.toolbar_ll);
        final TextView title = ViewUtils.findView(root, R.id.title);
        final ImageView home = ViewUtils.findView(root, R.id.back);
        final ImageView menu = ViewUtils.findView(root, R.id.menu);
        final ImageView item = ViewUtils.findView(root, R.id.item);
        final MaterialProgressBar progresBar = ViewUtils.findView(root, R.id.horizontalprogressbar);

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
        if (context != null) {
            mContext = new WeakReference<>(context);
        }
        if (toolbarLL != null) {
            mToolbarLL = new WeakReference<>(toolbarLL);
        }
        if (title != null) {
            mTitle = new WeakReference<>(title);
        }
        if (progresBar != null) {
            mHorizontalPogressBar = new WeakReference<>(progresBar);
        }
    }

    @Override
    public void onDestroyLifecycle() {
        super.onDestroyLifecycle();

        mContext = null;
        mToolbarLL = null;
        mTitle = null;
        mHorizontalPogressBar = null;
        mHome = null;
        mMenu = null;
        mItem = null;

        EventController.getInstance().removeSticky(new ToolbarInitEvent());
        EventController.getInstance().unregister(this);
    }

    @Override
    public boolean validate() {
        return (super.validate()
                && mContext != null && mContext.get() != null
                && mToolbarLL != null && mToolbarLL.get() != null
                && mTitle != null && mTitle.get() != null
                && mHorizontalPogressBar != null && mHorizontalPogressBar.get() != null
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
    public boolean isRegister() {
        return false;
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

        EventController.getInstance().postSticky(new ToolbarInitEvent());
    }

    private void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.item:
            case R.id.back:
                EventController.getInstance().post(new OnToolbarClickEvent(view));
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
            mPopupMenu = new PopupMenu(mContext.get(), view);
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
        EventController.getInstance().post(new OnToolbarMenuItemClickEvent(item));
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
        if (validate()) {
            ApplicationUtils.runOnUiThread(() -> {
                mBackNavigation = backNavigation;
                mHome.get().setVisibility(mBackNavigation ? View.VISIBLE : View.INVISIBLE);
                if (!mBackNavigation) {
                    mHome.get().setImageDrawable(ViewUtils.getDrawable(mContext.get(), R.mipmap.ic_menu));
                } else {
                    mHome.get().setImageDrawable(ViewUtils.getDrawable(mContext.get(), R.mipmap.ic_arrow_left));
                }
            });
        }
    }

    @Override
    public void resetToolbar() {
        ApplicationUtils.runOnUiThread(() -> resetToolbarTask());
    }

    @Override
    public void setHome(final int iconId, final boolean isVisible) {
        if (validate()) {
            mHome.get().setImageResource(iconId);
            mHome.get().setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    private void resetToolbarTask() {
        setShow(true);
        setHome(0, false);
        setTitle(0, null);
        setMenu(0, false);
        setItem(0, false);
        setBackNavigation(false);

        EventController.getInstance().post(new FragmentPrepareToolbarEvent());
    }

    @Override
    public boolean isShow() {
        return mShow;
    }

    @Override
    public void setShow(boolean isShow) {
        if (validate()) {
            mShow = isShow;

            ApplicationUtils.runOnUiThread(() -> {
                if (isShow()) {
                    mToolbarLL.get().setVisibility(View.VISIBLE);
                } else {
                    mToolbarLL.get().setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void showHorizontalProgressBar() {
        if (validate()) {
            final AbstractContentActivity activity = LifecycleController.getInstance().getContentActivity();
            if (activity != null) {
                ApplicationUtils.runOnUiThread(() -> {
                    final AbstractContentFragment fragment = activity.getContentFragment(AbstractContentFragment.class);
                    if (fragment != null) {
                        final ContentFragmentPresenter presenter = fragment.getContentFragmentPresenter();
                        if (presenter != null) {
                            final SwipeRefreshLayout swipeRefreshLayout = presenter.getSwipeRefreshLayout();
                            if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
                                mHorizontalPogressBar.get().setVisibility(View.VISIBLE);
                            }
                        } else {
                            mHorizontalPogressBar.get().setVisibility(View.VISIBLE);
                        }
                    } else {
                        mHorizontalPogressBar.get().setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    @Override
    public void hideHorizontalProgressBar() {
        if (validate()) {
            final AbstractContentActivity activity = LifecycleController.getInstance().getContentActivity();
            if (activity != null) {
                ApplicationUtils.runOnUiThread(() -> {
                    final AbstractContentFragment fragment = activity.getContentFragment(AbstractContentFragment.class);
                    if (fragment != null) {
                        final ContentFragmentPresenter presenter = fragment.getContentFragmentPresenter();
                        if (presenter != null) {
                            final SwipeRefreshLayout swipeRefreshLayout = presenter.getSwipeRefreshLayout();
                            if (swipeRefreshLayout != null) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }
                    mHorizontalPogressBar.get().setVisibility(View.INVISIBLE);
                });
            }
        }
    }

    @Override
    public void setItem(final int itemId, final boolean isVisible) {
        if (validate()) {
            ApplicationUtils.runOnUiThread(() -> {
                if (itemId > 0) {
                    mItem.get().setImageDrawable(ViewUtils.getDrawable(mContext.get(), itemId));
                }
                mItem.get().setVisibility(isVisible ? View.VISIBLE : View.GONE);
            });
        }
    }

    @Override
    public void setMenu(final int menuId, final boolean isVisible) {
        if (validate()) {
            ApplicationUtils.runOnUiThread(() -> {
                mMenuId = menuId;
                mMenu.get().setVisibility(isVisible ? View.VISIBLE : View.GONE);
            });
        }
    }

    @Override
    public void setTitle(final int iconId, final String title) {
        if (validate()) {
            ApplicationUtils.runOnUiThread(() -> {
                if (StringUtils.isNullOrEmpty(title)) {
                    mTitle.get().setText(StringUtils.EMPTY);
                } else {
                    mTitle.get().setText(title);
                }
                mTitle.get().setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSetToolbarBackNavigationEvent(ToolbarSetBackNavigationEvent event) {
        setBackNavigation(event.getBackNavigation());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onResetToolbarEvent(ToolbarResetEvent event) {
        resetToolbar();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSetToolbarTitleEvent(ToolbarSetTitleEvent event) {
        setTitle(event.getIconId(), event.getTitle());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSetToolbarMenuEvent(ToolbarSetMenuEvent event) {
        setMenu(event.getMenuId(), event.isVisible());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSetToolbarItemEvent(ToolbarSetItemEvent event) {
        setItem(event.getItemId(), event.isVisible());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onShowHorizontalProgressBarEvent(ShowHorizontalProgressBarEvent event) {
        showHorizontalProgressBar();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onHideHorizontalProgressBarEvent(HideHorizontalProgressBarEvent event) {
        hideHorizontalProgressBar();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onToolbarOnClickEvent(final OnToolbarClickEvent event) {
        final AbstractContentFragment fragment = NavigationController.getInstance().getContentFragment(AbstractContentFragment.class);
        if (fragment != null) {
            ApplicationUtils.runOnUiThread(() -> fragment.onClick(event.getView()));
        }
    }


}

package com.cleanarchitecture.shishkin.api.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.api.presenter.ToolbarPresenter;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

@SuppressWarnings("unused")
public abstract class AbstractContentDrawerFragment extends AbstractContentFragment
        implements View.OnClickListener, Drawer.OnDrawerItemClickListener {

    private Drawer mDrawer;

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isDrawerVisibled()) {
            final DrawerBuilder drawerBuilder = new DrawerBuilder()
                    .withActivity(getActivity())
                    .withOnDrawerListener(new Drawer.OnDrawerListener() {
                        @Override
                        public void onDrawerOpened(View drawerView) {
                            AdminUtils.postEvent(new HideKeyboardEvent());
                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {
                        }

                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {
                        }
                    })
                    .withOnDrawerItemClickListener(this);
            if (getHeaderDrawer() != 0) {
                drawerBuilder.withHeader(getHeaderDrawer());
            }
            if (getFooterDrawer() != 0) {
                drawerBuilder.withFooter(getFooterDrawer());
            }

            mDrawer = drawerBuilder.build();
        }
    }

    @Override
    public String getName() {
        return null;
    }

    public abstract boolean isDrawerVisibled();

    public abstract int getHeaderDrawer();

    public abstract int getFooterDrawer();

    public void addDrawerItem(IDrawerItem item) {
        mDrawer.addItem(item);
    }

    public void getDrawerItem(long id) {
        mDrawer.getDrawerItem(id);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.back:
                if (mDrawer != null && !((ToolbarPresenter) AdminUtils.getPresenter(ToolbarPresenter.NAME)).hasBackNavigation()) {
                    if (mDrawer.isDrawerOpen()) {
                        mDrawer.closeDrawer();
                    } else {
                        mDrawer.openDrawer();
                    }
                } else {
                    onBackPressed();
                }
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
            return true;
        } else {
            return super.onBackPressed();
        }
    }

}

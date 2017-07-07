package com.cleanarchitecture.shishkin.application.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.CheckDiskCacheEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseStartApplicationEvent;
import com.cleanarchitecture.shishkin.api.service.NotificationService;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractContentActivity;
import com.cleanarchitecture.shishkin.application.presenter.FloatingActionMenuPresenter;
import com.cleanarchitecture.shishkin.application.ui.fragment.HomeFragment;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

public class MainActivity extends AbstractContentActivity {

    public static final String NAME = MainActivity.class.getName();

    private FloatingActionMenuPresenter mFloatingActionMenuPresenter = new FloatingActionMenuPresenter();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        AdminUtils.postEvent(new UseCaseStartApplicationEvent());

        ViewUtils.setStatusBarColor(this, R.color.blue);

        setContentView(R.layout.activity_main);

        mFloatingActionMenuPresenter.bindView(findViewById(R.id.root));
        registerPresenter(mFloatingActionMenuPresenter);

        if (ViewUtils.isPhone(this)) {
            if (!ViewUtils.is6inchPhone(this)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                lockOrientation();
            }
        } else {
            if (ViewUtils.is10inchTablet(this)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                lockOrientation();
            }
        }

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if ("android.intent.action.MAIN".equalsIgnoreCase(action)) {
                // вызов из Launcher
                AdminUtils.postEvent(new CheckDiskCacheEvent());
                showHomeFragment();
            } else if (NotificationService.ACTION_CLICK.equalsIgnoreCase(action)) {
                // клик на сообщении в зоне уведомлений
                showHomeFragment();
            } else {
                showHomeFragment();
            }
        } else {
            showHomeFragment();
        }
    }

    private void showHomeFragment() {
        showFragment(HomeFragment.newInstance());
    }

    @Override
    public String getName() {
        return NAME;
    }
}

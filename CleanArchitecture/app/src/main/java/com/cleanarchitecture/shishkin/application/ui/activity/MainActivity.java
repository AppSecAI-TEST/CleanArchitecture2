package com.cleanarchitecture.shishkin.application.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.ui.fragment.HomeFragment;
import com.cleanarchitecture.shishkin.base.controller.NotificationService;
import com.cleanarchitecture.shishkin.base.event.ClearDiskCacheEvent;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractContentActivity;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.ViewUtils;

public class MainActivity extends AbstractContentActivity {

    public static final String NAME = "MainActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        ViewUtils.setStatusBarColor(this, R.color.blue);

        setContentView(R.layout.activity_main);

        if (ViewUtils.isPhone(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            if (ViewUtils.is7inchTablet(this)) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        lockOrientation();

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if ("android.intent.action.MAIN".equalsIgnoreCase(action)) {
                // вызов из Launcher
                ApplicationUtils.postEvent(new ClearDiskCacheEvent());
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

package com.cleanarchitecture.shishkin.api.presenter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryRequestGetApplicationSettingsEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryRequestSetApplicationSettingEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryResponseGetApplicationSettingsEvent;
import com.cleanarchitecture.shishkin.api.repository.data.ApplicationSetting;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;
import com.flyco.roundview.RoundRelativeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;

public class ApplicationSettingsPresenter extends AbstractPresenter<Void> implements CompoundButton.OnCheckedChangeListener {
    public static final String NAME = ApplicationSettingsPresenter.class.getName();

    private WeakReference<LinearLayout> mLinearLayout;
    private List<ApplicationSetting> mSettings;
    private LayoutInflater mInflater;

    public void bindView(@NonNull final View root) {
        if (root != null) {
            final AbstractActivity activity = AdminUtils.getActivity();
            if (activity != null) {
                mInflater = activity.getLayoutInflater();
            }

            final LinearLayout list = ViewUtils.findView(root, R.id.list);
            if (list != null) {
                mLinearLayout = new WeakReference<>(list);

                AdminUtils.postEvent(new RepositoryRequestGetApplicationSettingsEvent());
            }
        }
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

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean validate() {
        return (super.validate()
                && mLinearLayout != null && mLinearLayout.get() != null
        );
    }

    private void generateInfoItem(final ViewGroup parent, final ApplicationSetting setting) {
        View v = null;
        TextView titleView;
        String currentValue;
        RoundRelativeLayout colorView;

        switch (setting.getType()) {
            case ApplicationSetting.TYPE_TEXT:
                v = mInflater.inflate(AdminUtils.getLayoutId("setting_item_text", R.layout.setting_item_text), parent, false);
                titleView = ViewUtils.findView(v, R.id.item_title);
                titleView.setText(setting.getTitleId());
                break;

            case ApplicationSetting.TYPE_SWITCH:
                v = mInflater.inflate(AdminUtils.getLayoutId("setting_item_switch", R.layout.setting_item_switch), parent, false);
                titleView = ViewUtils.findView(v, R.id.item_title);
                titleView.setText(setting.getTitleId());

                final SwitchCompat valueView = ViewUtils.findView(v, R.id.item_switch);
                currentValue = setting.getCurrentValue();
                valueView.setChecked(Boolean.valueOf(currentValue));
                valueView.setTag(setting);
                valueView.setOnCheckedChangeListener(this);
                break;

            case ApplicationSetting.TYPE_COLOR:
                v = mInflater.inflate(AdminUtils.getLayoutId("setting_item_color", R.layout.setting_item_color), parent, false);
                titleView = ViewUtils.findView(v, R.id.item_title);
                titleView.setText(setting.getTitleId());
                titleView.setTag(setting);
                titleView.setOnClickListener(this::onClickChangeColor);

                colorView = ViewUtils.findView(v, R.id.item_color);
                final int color = Color.parseColor(setting.getCurrentValue());
                colorView.getDelegate().setBackgroundColor(color);
                colorView.setTag(setting);
                colorView.setOnClickListener(this::onClickChangeColor);
                break;
        }

        if (v != null) {
            parent.addView(v);
        }
    }

    private void onClickChangeColor(View view) {
        final ApplicationSetting setting = (ApplicationSetting)view.getTag();
        if (setting != null) {

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final ApplicationSetting setting = (ApplicationSetting) buttonView.getTag();
        if (setting != null) {
            setting.setCurrentValue(String.valueOf(isChecked));

            AdminUtils.postEvent(new RepositoryRequestSetApplicationSettingEvent(setting));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onRepositoryResponseGetApplicationSettingsEvent
            (RepositoryResponseGetApplicationSettingsEvent event) {
        if (event.getResponse() == null) {
            return;
        }

        mSettings = event.getResponse();
        if (validate()) {
            mLinearLayout.get().removeAllViews();
            for (ApplicationSetting setting : mSettings) {
                generateInfoItem(mLinearLayout.get(), setting);
            }
        }
    }
}

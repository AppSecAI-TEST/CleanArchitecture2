package com.cleanarchitecture.shishkin.api.presenter;

import android.content.Context;
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
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ApplicationSettingsPresenter extends AbstractPresenter<Void> implements CompoundButton.OnCheckedChangeListener {
    public static final String NAME = ApplicationSettingsPresenter.class.getName();

    private WeakReference<LinearLayout> mLinearLayout;
    private List<ApplicationSetting> mSettings = new ArrayList<>();

    public void bindView(@NonNull final View root) {
        if (root != null) {
            final LinearLayout list = ViewUtils.findView(root, R.id.list);
            if (list != null) {
                list.removeAllViews();
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
        final LayoutInflater inflater = AdminUtils.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(getInfoItemLayoutId(setting.getType()), parent, false);
        switch (setting.getType()) {
            case ApplicationSetting.TYPE_SWITCH:
                final TextView titleView = ViewUtils.findView(v, R.id.item_title);
                titleView.setText(setting.getTitleId());

                final SwitchCompat valueView = ViewUtils.findView(v, R.id.item_switch);
                final String currentValue = setting.getCurrentValue();
                valueView.setChecked(Boolean.valueOf(currentValue));
                valueView.setTag(setting);
                valueView.setOnCheckedChangeListener(this);
                break;
        }

        parent.addView(v);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final ApplicationSetting setting = (ApplicationSetting) buttonView.getTag();
        if (setting != null) {
            setting.setCurrentValue(String.valueOf(isChecked));

            AdminUtils.postEvent(new RepositoryRequestSetApplicationSettingEvent(setting));
        }
    }

    private int getInfoItemLayoutId(final int type) {
        switch (type) {
            case ApplicationSetting.TYPE_SWITCH:
                return R.layout.setting_item_layout;
        }
        return R.layout.setting_item_layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onRepositoryResponseGetApplicationSettingsEvent(RepositoryResponseGetApplicationSettingsEvent event) {
        if (validate()) {
            mSettings = event.getResponse();
            if (mSettings == null) {
                return;
            }

            for (ApplicationSetting setting : mSettings) {
                generateInfoItem(mLinearLayout.get(), setting);
            }
        }
    }


}

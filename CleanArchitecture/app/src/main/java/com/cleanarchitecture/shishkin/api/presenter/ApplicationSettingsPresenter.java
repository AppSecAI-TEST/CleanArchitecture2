package com.cleanarchitecture.shishkin.api.presenter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import com.cleanarchitecture.shishkin.api.data.ApplicationSetting;
import com.cleanarchitecture.shishkin.api.event.ShowFragmentEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryRequestGetApplicationSettingsEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryRequestSetApplicationSettingEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryResponseGetApplicationSettingsEvent;
import com.cleanarchitecture.shishkin.api.event.ui.DialogResultEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowListDialogEvent;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.api.ui.dialog.MaterialDialogExt;
import com.cleanarchitecture.shishkin.api.ui.fragment.SettingsColorPickerFragment;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;
import com.flyco.roundview.RoundRelativeLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
    public List<String> getSubscription() {
        return StringUtils.arrayToList(
                super.getSubscription(),
                EventBusController.NAME);
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
                titleView.setText(setting.getTitle());
                break;

            case ApplicationSetting.TYPE_SWITCH:
                v = mInflater.inflate(AdminUtils.getLayoutId("setting_item_switch", R.layout.setting_item_switch), parent, false);
                titleView = ViewUtils.findView(v, R.id.item_title);
                titleView.setText(setting.getTitle());

                final SwitchCompat valueView = ViewUtils.findView(v, R.id.item_switch);
                currentValue = setting.getCurrentValue();
                valueView.setChecked(Boolean.valueOf(currentValue));
                valueView.setTag(setting);
                valueView.setOnCheckedChangeListener(this);
                break;

            case ApplicationSetting.TYPE_COLOR:
                v = mInflater.inflate(AdminUtils.getLayoutId("setting_item_color", R.layout.setting_item_color), parent, false);
                ViewUtils.findView(v, R.id.ll).setTag(setting);
                ViewUtils.findView(v, R.id.ll).setOnClickListener(this::onClickChangeColor);

                titleView = ViewUtils.findView(v, R.id.item_title);
                titleView.setText(setting.getTitle());

                colorView = ViewUtils.findView(v, R.id.item_color);
                currentValue = setting.getCurrentValue();
                int color;
                if (currentValue.contains("#")) {
                    color = Color.parseColor(currentValue);
                } else {
                    color = Integer.parseInt(currentValue);
                }
                colorView.getDelegate().setBackgroundColor(color);
                break;

            case ApplicationSetting.TYPE_LIST:
                v = mInflater.inflate(AdminUtils.getLayoutId("setting_item_list", R.layout.setting_item_list), parent, false);
                ViewUtils.findView(v, R.id.ll).setTag(setting);
                ViewUtils.findView(v, R.id.ll).setOnClickListener(this::onClickList);
                titleView = ViewUtils.findView(v, R.id.item_title);
                titleView.setText(setting.getTitle());
                titleView = ViewUtils.findView(v, R.id.item_value);
                titleView.setText(setting.getCurrentValue());
                break;

        }

        if (v != null) {
            parent.addView(v);
        }
    }

    private void onClickList(View view) {
        final ApplicationSetting setting = (ApplicationSetting) view.getTag();
        if (setting != null) {
            AdminUtils.postEvent(new ShowListDialogEvent(setting.getId(), setting.getTitle(), null, setting.getValues(), MaterialDialogExt.NO_BUTTON, MaterialDialogExt.NO_BUTTON, true));
        }
    }

    private void onClickChangeColor(View view) {
        final ApplicationSetting setting = (ApplicationSetting) view.getTag();
        if (setting != null) {
            AdminUtils.postEvent(new ShowFragmentEvent(SettingsColorPickerFragment.newInstance(setting)));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDialogResultEvent(DialogResultEvent event) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            final Bundle bundle = event.getResult();
            if (bundle != null && bundle.getInt("id", -1) == R.id.application_setting_orientation) {
                final ArrayList<String> list = bundle.getStringArrayList("list");
                if (list != null && !list.isEmpty()) {
                    final String currentValue = AdminUtils.getPreferences().getOrientation();
                    final String[] keys = context.getResources().getStringArray(R.array.orientation_key);
                    final String[] values = context.getResources().getStringArray(R.array.orientation_value);
                    final String newKey = list.get(0);
                    for (int i = 0; i < keys.length; i++) {
                        if (keys[i].equals(newKey)) {
                            final String newValue = values[i];
                            if (!newValue.equals(currentValue)) {
                                AdminUtils.getPreferences().setOrientation(newValue);
                                final AbstractActivity activity = AdminUtils.getActivity();
                                if (activity != null) {
                                    activity.recreate();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

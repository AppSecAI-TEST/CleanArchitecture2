package com.cleanarchitecture.shishkin.api.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.data.ApplicationSetting;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryRequestSetApplicationSettingEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetBackNavigationEvent;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetTitleEvent;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

public class SettingsColorPickerFragment extends AbstractContentFragment implements ColorPicker.OnColorChangedListener {

    public static final String NAME = SettingsColorPickerFragment.class.getName();
    public static final String SETTING = "SETTING";

    private int mDefaultColor;
    private ApplicationSetting mSetting;
    private ColorPicker mPicker;

    public static SettingsColorPickerFragment newInstance(final ApplicationSetting setting) {
        final SettingsColorPickerFragment fragment = new SettingsColorPickerFragment();
        final Bundle args = new Bundle();
        args.putParcelable(SETTING, setting);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(AdminUtils.getLayoutId("fragment_color_picker_setting", R.layout.fragment_color_picker_setting), container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSetting = (ApplicationSetting) getArguments().getParcelable(SETTING);

        if (mSetting.getDefaultValue().contains("#")) {
            mDefaultColor = Color.parseColor(mSetting.getDefaultValue());
        } else {
            mDefaultColor = Integer.valueOf(mSetting.getDefaultValue());
        }
        int color;
        final String currentValue = mSetting.getCurrentValue();
        if (currentValue.contains("#")) {
            color = Color.parseColor(currentValue);
        } else {
            color = Integer.valueOf(currentValue);
        }
        final View button = ViewUtils.findView(view, R.id.button);
        button.setBackgroundColor(mDefaultColor);
        button.setOnClickListener(this::onClickButton);

        mPicker = ViewUtils.findView(view, R.id.color_picket);

        final SVBar svBar = ViewUtils.findView(view, R.id.svbar);
        final OpacityBar opacityBar = ViewUtils.findView(view, R.id.opacitybar);
        final SaturationBar saturationBar = ViewUtils.findView(view, R.id.saturationbar);
        final ValueBar valueBar = ViewUtils.findView(view, R.id.valuebar);

        mPicker.addSVBar(svBar);
        mPicker.addOpacityBar(opacityBar);
        mPicker.addSaturationBar(saturationBar);
        mPicker.addValueBar(valueBar);

        mPicker.setOldCenterColor(color);
        mPicker.setColor(color);
        mPicker.setOnColorChangedListener(this);

    }

    private void onClickButton(View view) {
        mPicker.setColor(mDefaultColor);
        mSetting.setCurrentValue(String.valueOf(mDefaultColor));
        AdminUtils.postEvent(new RepositoryRequestSetApplicationSettingEvent(mSetting));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void prepareToolbar() {
        AdminUtils.postEvent(new ToolbarSetTitleEvent(0, getString(R.string.settings_color_title)));
        AdminUtils.postEvent(new ToolbarSetBackNavigationEvent(true));
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onColorChanged(int color) {
        mSetting.setCurrentValue(String.valueOf(color));
        AdminUtils.postEvent(new RepositoryRequestSetApplicationSettingEvent(mSetting));
    }
}

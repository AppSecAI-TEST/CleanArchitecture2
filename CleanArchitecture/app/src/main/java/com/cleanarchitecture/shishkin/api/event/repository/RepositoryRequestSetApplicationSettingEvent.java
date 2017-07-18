package com.cleanarchitecture.shishkin.api.event.repository;

import com.cleanarchitecture.shishkin.api.data.ApplicationSetting;
import com.cleanarchitecture.shishkin.api.event.AbstractEvent;

public class RepositoryRequestSetApplicationSettingEvent extends AbstractEvent {
    private ApplicationSetting mSetting;

    public RepositoryRequestSetApplicationSettingEvent(ApplicationSetting setting) {
        mSetting = setting;
    }

    public ApplicationSetting getApplicationSetting() {
        return mSetting;
    }

}

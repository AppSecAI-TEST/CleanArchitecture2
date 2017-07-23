package com.cleanarchitecture.shishkin.application.data.livedata;

import android.content.Context;

import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.event.repository.RemoveCursorEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.api.model.AbstractContentProviderLiveData;
import com.cleanarchitecture.shishkin.api.model.AbstractCursorContentProviderLiveData;
import com.cleanarchitecture.shishkin.api.storage.CacheUtils;
import com.cleanarchitecture.shishkin.api.storage.IExpiredParcelableStorage;
import com.cleanarchitecture.shishkin.api.storage.IParcelableStorage;
import com.cleanarchitecture.shishkin.api.storage.ParcelableDiskCache;
import com.cleanarchitecture.shishkin.api.storage.ParcelableMemoryCache;
import com.cleanarchitecture.shishkin.application.Constant;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestCursorGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseCursorGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class CursorPhoneContactLiveData extends AbstractCursorContentProviderLiveData<List<PhoneContactItem>> {
    public static final String NAME = CursorPhoneContactLiveData.class.getName();

    public CursorPhoneContactLiveData() {
        super(PhoneContactDAO.CONTENT_URI);
    }

    @Override
    public void getData() {
        AdminUtils.postEvent(new RepositoryRequestCursorGetContactsEvent(50));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onResponseCursorGetContactsEvent(RepositoryResponseCursorGetContactsEvent event) {
        if (!event.hasError()) {
            setValue(event.getResponse());
        } else {
            ErrorController.getInstance().onError(event.getError());
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();

        AdminUtils.postEvent(new RemoveCursorEvent().setId(Constant.REPOSITORY_REQUEST_CURSOR_GET_CONTACTS_EVENT));
    }


}

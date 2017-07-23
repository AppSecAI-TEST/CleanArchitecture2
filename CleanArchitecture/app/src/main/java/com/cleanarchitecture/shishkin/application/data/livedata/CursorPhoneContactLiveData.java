package com.cleanarchitecture.shishkin.application.data.livedata;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.event.livedata.LiveDataHasDataEvent;
import com.cleanarchitecture.shishkin.api.event.repository.RemoveCursorEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideProgressBarEvent;
import com.cleanarchitecture.shishkin.api.model.AbstractCursorContentProviderLiveData;
import com.cleanarchitecture.shishkin.application.Constant;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestCursorGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestCursorHasContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseCursorGetContactsEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class CursorPhoneContactLiveData extends AbstractCursorContentProviderLiveData<List<PhoneContactItem>> {
    public static final String NAME = CursorPhoneContactLiveData.class.getName();
    private boolean mBOF = false;

    public CursorPhoneContactLiveData() {
        super(PhoneContactDAO.CONTENT_URI);
    }

    @Override
    public void getData() {
        clearValue();

        AdminUtils.postEvent(new RepositoryRequestCursorGetContactsEvent(50));
    }

    private void clearValue() {
        final List<PhoneContactItem> list = getValue();
        if (list != null) {
            list.clear();
            setValue(list);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void removeCursor() {
        AdminUtils.postEvent(new RemoveCursorEvent().setId(Constant.REPOSITORY_REQUEST_CURSOR_GET_CONTACTS_EVENT));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onResponseCursorGetContactsEvent(RepositoryResponseCursorGetContactsEvent event) {
        AdminUtils.postEvent(new HideProgressBarEvent());

        mBOF = event.isBOF();

        if (!event.hasError()) {
            List<PhoneContactItem> list = getValue();
            if (list == null) {
                list = new LinkedList<>();
            }
            list.addAll(event.getResponse());
            final Comparator<PhoneContactItem> comparator = (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName());
            setValue(AdminUtils.getTransformDataModule().sorted(list, comparator).toList());
        } else {
            ErrorController.getInstance().onError(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onLiveDataHasDataEvent(LiveDataHasDataEvent event) {
        if (event.getId() == Constant.REPOSITORY_REQUEST_CURSOR_GET_CONTACTS_EVENT) {
            if (!mBOF) {
                AdminUtils.postEvent(new RepositoryRequestCursorHasContactsEvent(50));
            }
        }
    }

}

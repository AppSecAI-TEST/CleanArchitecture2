package com.cleanarchitecture.shishkin.application.data.datastore;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryTerminateRequestEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.api.model.AbstractDatastore;
import com.cleanarchitecture.shishkin.application.Constant;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.data.livedata.PhoneContactLiveData;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetChangedContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetChangedContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetDeletedContactsEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PhoneContactDatastore extends AbstractDatastore<PhoneContactLiveData> {

    public static final String NAME = PhoneContactDatastore.class.getName();

    public PhoneContactDatastore(final PhoneContactLiveData data) {
        super(data);
    }

    @Override
    public void getData() {
        clearData();

        AdminUtils.postEvent(new ShowHorizontalProgressBarEvent());
        AdminUtils.postEvent(new RepositoryRequestGetContactsEvent(50));
    }

    @Override
    public void terminate() {
        AdminUtils.postEvent(new RepositoryTerminateRequestEvent().setId(Constant.REPOSITORY_GET_CONTACTS_EVENT));
    }

    @Override
    public void clearData() {
        getLiveData().postValue(new ArrayList<>());
    }

    @Override
    public void onChangeData() {
        AdminUtils.postEvent(new RepositoryRequestGetChangedContactsEvent(getLiveData().getValue()));
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void setSortedValue(final List<PhoneContactItem> list) {
        final Comparator<PhoneContactItem> comparator = (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName());
        getLiveData().postValue(AdminUtils.getTransformDataModule().sorted(list, comparator).toList());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onResponseGetContactsEvent(RepositoryResponseGetContactsEvent event) {
        AdminUtils.postEvent(new HideHorizontalProgressBarEvent());
        if (!event.hasError()) {
            List<PhoneContactItem> list = getLiveData().getValue();
            if (list == null) {
                list = new ArrayList<>();
            }
            list.addAll(event.getResponse());
            setSortedValue(list);
        } else {
            ErrorController.getInstance().onError(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onResponseGetChangedContactsEvent(RepositoryResponseGetChangedContactsEvent event) {
        List<PhoneContactItem> list = getLiveData().getValue();
        if (list == null) {
            list = new ArrayList<>();
        }
        for (PhoneContactItem item : event.getResponse()) {
            int pos = list.indexOf(item);
            if (pos >= 0) {
                list.set(pos, item);
            } else {
                list.add(item);
            }
        }
        setSortedValue(list);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onResponseGetDeletedContactsEvent(RepositoryResponseGetDeletedContactsEvent event) {
        List<PhoneContactItem> list = getLiveData().getValue();
        if (list == null) {
            list = new ArrayList<>();
        }
        for (PhoneContactItem item : event.getResponse()) {
            int pos = list.indexOf(item);
            if (pos >= 0) {
                list.remove(pos);
            }
        }
        getLiveData().postValue(list);
    }
}

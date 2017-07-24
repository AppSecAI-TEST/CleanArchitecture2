package com.cleanarchitecture.shishkin.application.data.livedata;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.event.repository.RepositoryTerminateRequestEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.api.model.AbstractCursorContentProviderLiveData;
import com.cleanarchitecture.shishkin.application.Constant;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestCursorGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class CursorPhoneContactLiveData extends AbstractCursorContentProviderLiveData<List<PhoneContactItem>> {
    public static final String NAME = CursorPhoneContactLiveData.class.getName();

    public CursorPhoneContactLiveData() {
        super(PhoneContactDAO.CONTENT_URI);
    }

    @Override
    public void getData() {
        AdminUtils.postEvent(new ShowHorizontalProgressBarEvent());
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
    public void terminate() {
        AdminUtils.postEvent(new RepositoryTerminateRequestEvent().setId(Constant.REPOSITORY_REQUEST_CURSOR_GET_CONTACTS_EVENT));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onResponseGetContactsEvent(RepositoryResponseGetContactsEvent event) {
        AdminUtils.postEvent(new HideHorizontalProgressBarEvent());

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

}

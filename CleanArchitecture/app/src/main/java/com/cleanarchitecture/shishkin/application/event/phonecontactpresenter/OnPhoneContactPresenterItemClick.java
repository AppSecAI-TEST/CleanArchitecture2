package com.cleanarchitecture.shishkin.application.event.phonecontactpresenter;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;

public class OnPhoneContactPresenterItemClick extends AbstractEvent {
    private PhoneContactItem mContactItem;

    public OnPhoneContactPresenterItemClick(final PhoneContactItem contactItem) {
        mContactItem = contactItem;
    }

    public PhoneContactItem getContactItem() {
        return mContactItem;
    }
}

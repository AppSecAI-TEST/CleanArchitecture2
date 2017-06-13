package com.cleanarchitecture.shishkin.application.event.searchpresenter;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;

public class OnSearchPresenterItemClick extends AbstractEvent {
    private PhoneContactItem mContactItem;

    public OnSearchPresenterItemClick(final PhoneContactItem contactItem) {
        mContactItem = contactItem;
    }

    public PhoneContactItem getContactItem() {
        return mContactItem;
    }
}

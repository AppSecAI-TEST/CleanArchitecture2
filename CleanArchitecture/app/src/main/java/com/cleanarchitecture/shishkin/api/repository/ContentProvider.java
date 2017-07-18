package com.cleanarchitecture.shishkin.api.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.data.ExtError;
import com.cleanarchitecture.shishkin.api.event.IEvent;
import com.cleanarchitecture.shishkin.application.Constant;
import com.cleanarchitecture.shishkin.application.data.dao.PhoneContactDAO;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;

public class ContentProvider extends AbstractModule implements IContentProvider {
    public static final String NAME = ContentProvider.class.getName();

    @NonNull
    public synchronized IEvent getContacts() {
        final RepositoryResponseGetContactsEvent event = (RepositoryResponseGetContactsEvent) new RepositoryResponseGetContactsEvent()
                .setId(Constant.REPOSITORY_GET_CONTACTS);

        final Context context = AdminUtils.getContext();
        if (context == null) {
            return event.setError(new ExtError().addError(NAME, ErrorController.ERROR_LOST_APPLICATION_CONTEXT));
        }

        return event.setResponse(new PhoneContactDAO(context).getItems(context));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return null;
    }

}

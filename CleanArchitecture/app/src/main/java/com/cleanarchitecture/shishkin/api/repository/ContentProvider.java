package com.cleanarchitecture.shishkin.api.repository;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.event.IEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.api.repository.data.ExtError;
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
            return event.setError(new ExtError().setErrorCode(NAME, ErrorController.ERROR_LOST_APPLICATION_CONTEXT));
        }

        if (!AdminUtils.checkPermission(Manifest.permission.READ_CONTACTS)) {
            AdminUtils.postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.READ_CONTACTS));
            return event.setError(new ExtError().setErrorText(NAME, context.getString(R.string.permission_read_contacts)));
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

package com.cleanarchitecture.shishkin.application.database.content;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.application.database.CleanArchitectureDatabaseHelper;
import com.cleanarchitecture.shishkin.application.database.dao.ConfigDAO;
import com.cleanarchitecture.shishkin.application.database.dao.SqliteMasterDAO;
import com.cleanarchitecture.shishkin.base.content.AbstractContentProvider;
import com.cleanarchitecture.shishkin.base.database.IDatabaseOpenHelper;

public class CleanArchitectureContentProvider extends AbstractContentProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".sqlite.contentprovider";

    @Override
    public void onAttachInfo(final Context context, final ProviderInfo info) {
        final String authority = info.authority;
        addUri(authority, SqliteMasterDAO.TABLE, true);
        addUri(authority, ConfigDAO.TABLE, true);
    }

    @NonNull
    @Override
    public IDatabaseOpenHelper onCreateDatabaseOpenHelper(@NonNull final Context context) {
        return new CleanArchitectureDatabaseHelper(context);
    }
}

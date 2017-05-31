package com.cleanarchitecture.shishkin.application.database;

import android.content.Context;
import android.support.annotation.NonNull;

import com.cleanarchitecture.shishkin.application.database.dao.ConfigDAO;
import com.cleanarchitecture.shishkin.application.database.item.ConfigItem;
import com.cleanarchitecture.shishkin.base.controller.Controllers;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.database.Column;
import com.cleanarchitecture.shishkin.base.database.ConflictResolution;
import com.cleanarchitecture.shishkin.base.database.DatabaseCreator;
import com.cleanarchitecture.shishkin.base.database.IDatabase;
import com.cleanarchitecture.shishkin.base.database.Table;
import com.cleanarchitecture.shishkin.base.event.database.DbCreatedEvent;
import com.cleanarchitecture.shishkin.base.event.database.DbUpdatedEvent;

public class CleanArchitectureDatabaseCreator extends DatabaseCreator {
    private static final String LOG_TAG = "CleanArchitectureDatabaseCreator:";

    public CleanArchitectureDatabaseCreator() {
        super();
    }

    @Override
    public void onCreate(@NonNull final Context context, final IDatabase db) {
        createConfig(context, db);
        EventBusController.getInstance().post(new DbCreatedEvent(db.getName()));
    }

    @Override
    public boolean onUpgrade(@NonNull final Context context, final IDatabase db, final int toVersion) {
        switch (toVersion) {
        }
        EventBusController.getInstance().post(new DbUpdatedEvent(db.getName()));
        return true;
    }

    private void createConfig(@NonNull Context context, IDatabase db) {
        if (!isTableExists(context, ConfigDAO.CONTENT_URI)) {
            Table.forName(ConfigDAO.TABLE)
                    .addColumn(Column.text(ConfigDAO.Columns.RowId).unique(ConflictResolution.ABORT))
                    .addColumn(Column.integer(ConfigDAO.Columns.Version))
                    .create(db);

            Table.createIndex(db, ConfigDAO.TABLE, ConfigDAO.TABLE + "_" + ConfigDAO.Columns.RowId, new String[]{ConfigDAO.Columns.RowId});

            new ConfigDAO(context).insert(new ConfigItem("1", CleanArchitectureDatabaseHelper.DATABASE_VERSION));
        }
    }

}

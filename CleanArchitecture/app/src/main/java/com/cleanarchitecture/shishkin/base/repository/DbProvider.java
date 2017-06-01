package com.cleanarchitecture.shishkin.base.repository;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.database.CleanArchitectureDb;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.controller.ISubscriber;
import com.cleanarchitecture.shishkin.base.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.utils.SafeUtils;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;
import com.github.snowdream.android.util.Log;
import com.google.common.io.Files;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DbProvider implements ISubscriber {
    public static final String NAME = "DbProvider";

    private Map<String, Object> mDb;

    public DbProvider() {
        mDb = Collections.synchronizedMap(new HashMap<String, Object>());

        EventBusController.getInstance().register(this);
    }

    private synchronized <T extends RoomDatabase> boolean connect(final Class<T> klass, final String databaseName) {
        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return false;
        }

        if (isConnected(databaseName)) {
            disconnect(databaseName);
        }

        try {
            final T db = Room.databaseBuilder(context, klass, databaseName)
                    .build();
            mDb.put(databaseName, db);
        } catch (Exception e) {
            Log.e(NAME, e.getMessage());
        }
        return isConnected(databaseName);
    }

    private synchronized boolean isConnected(final String databaseName) {
        if (StringUtils.isNullOrEmpty(databaseName)) {
            return false;
        }

        return mDb.containsKey(databaseName);
    }

    private synchronized <T extends RoomDatabase> boolean disconnect(final String databaseName) {
        if (isConnected(databaseName)) {
            final T db = SafeUtils.cast(mDb.get(databaseName));
            try {
                db.close();
                mDb.remove(databaseName);
            } catch (Exception e) {
                Log.e(NAME, e.getMessage());
            }
        }
        return !isConnected(databaseName);
    }

    public synchronized <T extends RoomDatabase> void backup(final String databaseName, final String dirBackup) {
        final T db = SafeUtils.cast(mDb.get(databaseName));
        if (db == null) {
            return;
        }

        final Class<T> klass = SafeUtils.cast(db.getClass().getSuperclass());
        final String pathDb = db.getOpenHelper().getReadableDatabase().getPath();
        if (StringUtils.isNullOrEmpty(pathDb)) {
            return;
        }

        disconnect(databaseName);

        final File fileDb = new File(pathDb);
        final String nameDb = fileDb.getName();
        final String pathBackup = dirBackup + File.separator + nameDb;
        try {
            final File fileBackup = new File(pathBackup);
            final File fileBackupOld = new File(pathBackup + "1");
            if (fileDb.exists()) {
                if (fileBackup.exists()) {
                    final File dir = new File(dirBackup);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    if (dir.exists()) {
                        if (fileBackupOld.exists()) {
                            fileBackupOld.delete();
                        }
                        if (!fileBackupOld.exists()) {
                            Files.copy(fileBackup, fileBackupOld);
                            if (fileBackupOld.exists()) {
                                fileBackup.delete();
                                if (!fileBackup.exists()) {
                                    Files.copy(fileDb, fileBackup);
                                    if (fileBackup.exists()) {
                                        fileBackupOld.delete();
                                    } else {
                                        Files.copy(fileBackupOld, fileBackup);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    final File dir = new File(dirBackup);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    if (dir.exists()) {
                        Files.copy(fileDb, fileBackup);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(NAME, e.getMessage());
        }

        connect(klass, nameDb);
    }

    public synchronized <T extends RoomDatabase> void restore(final String databaseName, final String dirBackup) {
        final T db = SafeUtils.cast(mDb.get(databaseName));
        if (db == null) {
            return;
        }

        final Class<T> klass = SafeUtils.cast(db.getClass().getSuperclass());
        final String pathDb = db.getOpenHelper().getReadableDatabase().getPath();
        if (StringUtils.isNullOrEmpty(pathDb)) {
            return;
        }

        disconnect(databaseName);

        final File fileDb = new File(pathDb);
        final String nameDb = fileDb.getName();
        final String pathBackup = dirBackup + File.separator + nameDb;
        final File fileBackup = new File(pathBackup);
        if (fileBackup.exists()) {
            try {
                if (fileDb.exists()) {
                    fileDb.delete();
                }
                if (!fileDb.exists()) {
                    Files.createParentDirs(fileDb);
                    final File dir = new File(fileDb.getParent());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    if (dir.exists()) {
                        Files.copy(fileBackup, fileDb);
                    }
                }
            } catch (Exception e) {
                Log.e(NAME, e.getMessage());
            }
        }

        connect(klass, nameDb);
    }

    public <T extends RoomDatabase> T getDb(final Class<T> klass, final String databaseName) {
        if (!isConnected(databaseName)) {
            connect(klass, databaseName);
        }
        return SafeUtils.cast(mDb.get(databaseName));
    }

    @Override
    public String getName() {
        return NAME;
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onFinishApplicationEvent(final FinishApplicationEvent event) {
        while (!mDb.isEmpty()) {
            for (String databaseName : mDb.keySet()) {
                disconnect(databaseName);
                break;
            }
        }
    }
}

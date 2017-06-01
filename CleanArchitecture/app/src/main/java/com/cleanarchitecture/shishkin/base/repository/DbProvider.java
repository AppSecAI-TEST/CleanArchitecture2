package com.cleanarchitecture.shishkin.base.repository;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.database.CleanArchitectureDb;
import com.cleanarchitecture.shishkin.base.controller.ISubscriber;
import com.cleanarchitecture.shishkin.base.utils.SafeUtils;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;
import com.github.snowdream.android.util.Log;
import com.google.common.io.Files;

import java.io.File;

public class DbProvider<T extends RoomDatabase> implements ISubscriber {
    public static final String NAME = "DbProvider";

    private T mDb;

    public DbProvider() {
        connect(CleanArchitectureDb.class, CleanArchitectureDb.NAME);
    }

    public synchronized <T extends RoomDatabase> boolean connect(final Class<T> klass, final String databaseName) {
        if (isConnected()) {
            disconnect();
        }

        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return false;
        }

        try {
            mDb = SafeUtils.cast(Room.databaseBuilder(context, klass, databaseName)
                    .build());
        } catch (Exception e) {
            Log.e(NAME, e.getMessage());
        }
        return isConnected();
    }

    public synchronized boolean isConnected() {
        return (mDb != null);
    }

    public synchronized boolean disconnect() {
        if (isConnected()) {
            try {
                mDb.close();
                mDb = null;
            } catch (Exception e) {
                Log.e(NAME, e.getMessage());
            }
        }
        return !isConnected();
    }

    public synchronized boolean exists(final String databaseName) {
        if (StringUtils.isNullOrEmpty(databaseName)) {
            return false;
        }

        final Context context = ApplicationController.getInstance();
        if (context == null) {
            return false;
        }

        try {
            final String pathDb = context.getDatabasePath(databaseName).getAbsolutePath();
            if (StringUtils.isNullOrEmpty(pathDb)) {
                return false;
            }

            final File file = new File(pathDb);
            if (file.exists() && file.length() > 0) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }


    public synchronized boolean backup(final String dirBackup) {
        if (!isConnected()) {
            return false;
        }

        final String pathDb = mDb.getOpenHelper().getReadableDatabase().getPath();
        if (StringUtils.isNullOrEmpty(pathDb)) {
            return false;
        }

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
                                    return true;
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
                        if (fileBackup.exists()) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(NAME, e.getMessage());
        }
        return false;
    }

    public boolean restore(String dirBackup) {
        if (!isConnected()) {
            return false;
        }

        final String pathDb = mDb.getOpenHelper().getReadableDatabase().getPath();
        if (StringUtils.isNullOrEmpty(pathDb)) {
            return false;
        }

        if (!disconnect()) {
            return false;
        }

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

        connect(SafeUtils.cast(mDb.getClass()), nameDb);
        return isConnected();
    }


    @Override
    public String getName() {
        return NAME;
    }
}

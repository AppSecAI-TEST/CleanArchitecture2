package com.cleanarchitecture.shishkin.base.repository;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.ErrorController;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.base.data.AbstractViewModel;
import com.cleanarchitecture.shishkin.base.data.ViewModelDebounce;
import com.cleanarchitecture.shishkin.base.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.SafeUtils;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;
import com.google.common.io.Files;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbProvider<H extends AbstractViewModel> implements IDbProvider, LifecycleOwner, IModuleSubscriber {
    public static final String NAME = "DbProvider";

    private Map<String, Object> mDb;
    private Map<String, H> mViewModel;
    private LifecycleRegistry mLifecycleRegistry;

    public DbProvider() {
        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);

        mDb = Collections.synchronizedMap(new HashMap<String, Object>());
        mViewModel = Collections.synchronizedMap(new HashMap<String, H>());

        mLifecycleRegistry.markState(Lifecycle.State.STARTED);
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
            ErrorController.getInstance().onError(NAME, e, ErrorController.ERROR_DB);
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
                ErrorController.getInstance().onError(NAME, e, ErrorController.ERROR_DB);
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
            ErrorController.getInstance().onError(NAME, e, ErrorController.ERROR_DB);
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
                ErrorController.getInstance().onError(NAME, e, ErrorController.ERROR_DB);
            }
        }

        connect(klass, nameDb);
    }

    public synchronized <T extends RoomDatabase> T getDb(final Class<T> klass, final String databaseName) {
        if (!isConnected(databaseName)) {
            connect(klass, databaseName);
        }
        return SafeUtils.cast(mDb.get(databaseName));
    }

    @Override
    public synchronized <T, E extends AbstractViewModel> void observe(final LifecycleActivity activity, final String nameViewModel, final Class<E> klass, final IObserver<T> observer) {
        try {
            E viewModel = null;
            if (!mViewModel.containsKey(nameViewModel)) {
                viewModel = ViewModelProviders.of(activity).get(klass);
                viewModel.getLiveData().observe(this, observer);
                mViewModel.put(viewModel.getName(), (H) viewModel);
            } else {
                viewModel = (E) mViewModel.get(nameViewModel);
                viewModel.getLiveData().observe(this, observer);
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(NAME, e, ErrorController.ERROR_GET_DATA);
        }
    }

    @Override
    public synchronized <E extends AbstractViewModel, T> void removeObserver(final String nameViewModel, final IObserver<T> observer) {
        ApplicationUtils.runOnUiThread(() -> {
            try {
                if (mViewModel.containsKey(nameViewModel)) {
                    final E viewModel = (E) mViewModel.get(nameViewModel);
                    viewModel.getLiveData().removeObserver(observer);
                    if (!viewModel.getLiveData().hasObservers()) {
                        new ViewModelDebounce(nameViewModel).onEvent(nameViewModel);
                    }
                }
            } catch (Exception e) {
                ErrorController.getInstance().onError(NAME, e, ErrorController.ERROR_GET_DATA);
            }
        });
    }

    @Override
    public <E extends AbstractViewModel> E getViewModel(final String nameViewModel) {
        if (mViewModel.containsKey(nameViewModel)) {
            return (E) mViewModel.get(nameViewModel);
        }
        return null;
    }

    @Override
    public void removeViewModel(final String nameViewModel) {
        try {
            if (mViewModel.containsKey(nameViewModel)) {
                final H viewModel = mViewModel.get(nameViewModel);
                if (!viewModel.getLiveData().hasObservers()) {
                    mViewModel.remove(nameViewModel);
                }
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(NAME, e, ErrorController.ERROR_GET_DATA);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return null;
    }

    @Override
    public List<String> hasSubscriberType() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onFinishApplicationEvent(final FinishApplicationEvent event) {
        ApplicationUtils.runOnUiThread(() -> {
            for (H viewModel : mViewModel.values()) {
                viewModel.getLiveData().removeObservers(DbProvider.this);
            }
            mViewModel.clear();

            while (!mDb.isEmpty()) {
                for (String databaseName : mDb.keySet()) {
                    disconnect(databaseName);
                    break;
                }
            }
        });
    }

}

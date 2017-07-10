package com.cleanarchitecture.shishkin.api.repository;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.IModuleSubscriber;
import com.cleanarchitecture.shishkin.api.model.AbstractViewModel;
import com.cleanarchitecture.shishkin.api.model.ViewModelDebounce;
import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.SafeUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.google.common.io.Files;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DbProvider<H extends AbstractViewModel, T extends RoomDatabase> extends AbstractModule implements IDbProvider<H, T>, LifecycleOwner, IModuleSubscriber {
    public static final String NAME = DbProvider.class.getName();
    private static final String LOG_TAG = "DbProvider:";

    private Map<String, T> mDb;
    private Map<String, H> mViewModel;
    private LifecycleRegistry mLifecycleRegistry;

    public DbProvider() {
        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);

        mDb = Collections.synchronizedMap(new ConcurrentHashMap<String, T>());
        mViewModel = Collections.synchronizedMap(new ConcurrentHashMap<String, H>());

        mLifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    private synchronized boolean connect(final Class<T> klass, final String databaseName) {
        final Context context = AdminUtils.getContext();
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
            ErrorController.getInstance().onError(LOG_TAG, e, ErrorController.ERROR_DB);
        }
        return isConnected(databaseName);
    }

    private synchronized boolean isConnected(final String databaseName) {
        if (StringUtils.isNullOrEmpty(databaseName)) {
            return false;
        }

        return mDb.containsKey(databaseName);
    }

    private synchronized boolean disconnect(final String databaseName) {
        if (isConnected(databaseName)) {
            final T db = mDb.get(databaseName);
            try {
                db.close();
                mDb.remove(databaseName);
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e, ErrorController.ERROR_DB);
            }
        }
        return !isConnected(databaseName);
    }

    @Override
    public synchronized void backup(final String databaseName, final String dirBackup) {
        final T db = mDb.get(databaseName);
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
            ErrorController.getInstance().onError(LOG_TAG, e, ErrorController.ERROR_DB);
        }

        connect(klass, nameDb);
    }

    @Override
    public synchronized void restore(final String databaseName, final String dirBackup) {
        final T db = mDb.get(databaseName);
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
                ErrorController.getInstance().onError(LOG_TAG, e, ErrorController.ERROR_DB);
            }
        }

        connect(klass, nameDb);
    }

    @Override
    public synchronized T getDb(final Class<T> klass, final String databaseName) {
        if (!isConnected(databaseName)) {
            connect(klass, databaseName);
        }
        return mDb.get(databaseName);
    }

    @Override
    public synchronized <E> void observe(final LifecycleActivity activity, final String nameViewModel, final Class<H> klass, final IObserver<E> observer) {
        try {
            if (activity != null) {
                H viewModel;
                if (!mViewModel.containsKey(nameViewModel)) {
                    viewModel = ViewModelProviders.of(activity).get(klass);
                    viewModel.getLiveData().observe(this, observer);
                    mViewModel.put(viewModel.getName(), (H) viewModel);
                } else {
                    viewModel = mViewModel.get(nameViewModel);
                    viewModel.getLiveData().observe(this, observer);
                }
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e, ErrorController.ERROR_GET_DATA);
        }
    }

    @Override
    public synchronized <E> void removeObserver(final String nameViewModel, final IObserver<E> observer) {
        ApplicationUtils.runOnUiThread(() -> {
            try {
                if (mViewModel.containsKey(nameViewModel)) {
                    final H viewModel = mViewModel.get(nameViewModel);
                    viewModel.getLiveData().removeObserver(observer);
                    if (!viewModel.getLiveData().hasObservers()) {
                        new ViewModelDebounce(nameViewModel).onEvent();
                    }
                }
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e, ErrorController.ERROR_GET_DATA);
            }
        });
    }

    @Override
    public H getViewModel(final String nameViewModel) {
        if (mViewModel.containsKey(nameViewModel)) {
            return mViewModel.get(nameViewModel);
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
            ErrorController.getInstance().onError(LOG_TAG, e, ErrorController.ERROR_GET_DATA);
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

            for (String databaseName : mDb.keySet()) {
                disconnect(databaseName);
            }
        });
    }

}

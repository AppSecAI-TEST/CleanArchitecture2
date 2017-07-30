package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.api.repository.ContentProvider;
import com.cleanarchitecture.shishkin.api.repository.DbProvider;
import com.cleanarchitecture.shishkin.api.repository.NetProvider;
import com.cleanarchitecture.shishkin.api.repository.Repository;
import com.cleanarchitecture.shishkin.api.storage.ParcelableDiskCache;
import com.cleanarchitecture.shishkin.api.storage.ParcelableMemoryCache;
import com.cleanarchitecture.shishkin.api.storage.SerializableDiskCache;
import com.cleanarchitecture.shishkin.api.storage.SerializableMemoryCache;
import com.cleanarchitecture.shishkin.api.usecases.UseCasesController;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;

@SuppressWarnings("unused")
public class Admin extends AbstractAdmin {
    public static final String NAME = Admin.class.getName();
    private static final int MIN_HEAP_SIZE = 48; // 48Mb мимимальный размер Java Heap Size для старта кэшей в памяти

    private static volatile Admin sInstance;

    public static void instantiate() {
        if (sInstance == null) {
            synchronized (Admin.class) {
                if (sInstance == null) {
                    sInstance = new Admin();
                }
            }
        }
    }

    public static Admin getInstance() {
        if (sInstance == null) {
            instantiate();
        }
        return sInstance;
    }

    private Admin() {
        // default persistent (Singleton) controllers

        // Модуль приложения
        registerModule(ApplicationController.getInstance());

        // Модуль регистрации ошибок в приложении
        registerModule(ErrorController.getInstance());

        // Шина сообщений
        registerModule(EventBusController.getInstance());

        // Application Preferences
        registerModule(PreferencesModule.getInstance());

        // Кэш в памяти Serializable
        if (ApplicationUtils.getHeapSize() > MIN_HEAP_SIZE) {
            registerModule(SerializableMemoryCache.getInstance());
        }

        // Кэш в памяти Parcelable
        if (ApplicationUtils.getHeapSize() > MIN_HEAP_SIZE) {
            registerModule(ParcelableMemoryCache.getInstance());
        }

        // Кэш на диске Serializable
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            registerModule(SerializableDiskCache.getInstance(context));
        }

        // Кэш на диске Parcelable
        registerModule(ParcelableDiskCache.getInstance());

        // other controllers

        // Контроллер регистрации креша приложения
        registerModule(CrashController.NAME);

        // Контроллер Activity
        registerModule(ActivityController.NAME);

        // Контроллер презенторов
        registerModule(PresenterController.NAME);

        // Контроллер навигации
        registerModule(NavigationController.NAME);

        // Контроллер бизнес логики
        registerModule(UseCasesController.NAME);

        // Контроллер почтовых сообщений объектов
        registerModule(MailController.NAME);

        // Контроллер пользовательских действий
        registerModule(UserInteractionController.NAME);

        // Модуль выбрки данных из ContentProvider
        registerModule(ContentProvider.NAME);

        // Модуль выбрки данных из БД
        registerModule(DbProvider.NAME);

        // Модуль выбрки данных из сети
        registerModule(NetProvider.NAME);

        // Модуль хранилища данных
        registerModule(Repository.NAME);

        // Модуль работы с рабочими столами
        registerModule(DesktopController.NAME);

        // Модуль работы с геолокацией
        registerModule(LocationController.NAME);

        // Модуль преобразования данных
        registerModule(TransformDataModule.NAME);

        // Модуль валидации данных
        registerModule(ValidateController.NAME);

    }

    @Override
    public String getName() {
        return NAME;
    }

}

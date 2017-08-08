package com.cleanarchitecture.shishkin.api.usecases;

import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.IPresenterController;
import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.api.storage.IParcelableStorage;
import com.cleanarchitecture.shishkin.api.storage.ISerializableStorage;
import com.cleanarchitecture.shishkin.api.storage.ParcelableMemoryCache;
import com.cleanarchitecture.shishkin.api.storage.SerializableMemoryCache;

/**
 * Команда - выход из приложения
 */
public class FinishApplicationUseCase extends AbstractUseCase {
    public static final String NAME = FinishApplicationUseCase.class.getName();

    public static synchronized void onFinishApplication() {

        AdminUtils.postEvent(new HideKeyboardEvent());

        // finish all activities
        AdminUtils.postEvent(new FinishApplicationEvent());

        // очистить кэши в памяти
        final ISerializableStorage serializableStorage = Admin.getInstance().get(SerializableMemoryCache.NAME);
        if (serializableStorage != null) {
            serializableStorage.clear();
        }

        final IParcelableStorage parcelableStorage = Admin.getInstance().get(ParcelableMemoryCache.NAME);
        if (parcelableStorage != null) {
            parcelableStorage.clear();
        }

        // очистить кэш состояний презенторов
        final IPresenterController presenterController = AdminUtils.getPresenterController();
        if (presenterController != null) {
            presenterController.clearStateData();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}

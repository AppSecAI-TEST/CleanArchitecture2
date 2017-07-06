package com.cleanarchitecture.shishkin.api.usecases;

import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.storage.ISerializableStorage;
import com.cleanarchitecture.shishkin.api.storage.SerializableMemoryCache;

/**
 * Команда - мало памяти у приложения
 */
public class LowMemoryUseCase extends AbstractUseCase {

    public static final String NAME = LowMemoryUseCase.class.getName();

    public static synchronized void onLowMemory() {
        final ISerializableStorage memoryCache = Admin.getInstance().get(SerializableMemoryCache.NAME);
        if (memoryCache != null) {
            memoryCache.clear();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}

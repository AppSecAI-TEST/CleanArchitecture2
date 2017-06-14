package com.cleanarchitecture.shishkin.api.usecases;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.storage.IStorage;

/**
 * Команда - мало памяти у приложения
 */
public class LowMemoryUseCase extends AbstractUseCase {

    public static final String NAME = LowMemoryUseCase.class.getName();

    public static synchronized void onLowMemory() {
        final IStorage memoryCache = AdminUtils.getMemoryCache();
        if (memoryCache != null) {
            memoryCache.clearAll();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}

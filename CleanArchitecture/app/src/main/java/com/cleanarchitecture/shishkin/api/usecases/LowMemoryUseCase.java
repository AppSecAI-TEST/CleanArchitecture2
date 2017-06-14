package com.cleanarchitecture.shishkin.api.usecases;

import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.storage.IStorage;
import com.cleanarchitecture.shishkin.api.storage.MemoryCache;

/**
 * Команда - мало памяти у приложения
 */
public class LowMemoryUseCase extends AbstractUseCase {

    public static final String NAME = LowMemoryUseCase.class.getName();

    public static synchronized void onLowMemory() {
        final IStorage memoryCache = Admin.getInstance().get(MemoryCache.NAME);
        if (memoryCache != null) {
            memoryCache.clearAll();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}

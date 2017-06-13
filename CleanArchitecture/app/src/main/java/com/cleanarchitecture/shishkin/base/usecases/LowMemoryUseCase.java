package com.cleanarchitecture.shishkin.base.usecases;

import com.cleanarchitecture.shishkin.base.controller.Admin;
import com.cleanarchitecture.shishkin.base.storage.IStorage;
import com.cleanarchitecture.shishkin.base.storage.MemoryCache;

/**
 * Команда - мало памяти у приложения
 */
public class LowMemoryUseCase extends AbstractUseCase {

    public static final String NAME = "LowMemoryUseCase";

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

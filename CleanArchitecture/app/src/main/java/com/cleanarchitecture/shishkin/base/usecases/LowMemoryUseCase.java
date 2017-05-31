package com.cleanarchitecture.shishkin.base.usecases;

import com.cleanarchitecture.shishkin.base.storage.MemoryCache;

/**
 * Команда - мало памяти у приложения
 */
public class LowMemoryUseCase extends AbstractUseCase {

    public static final String NAME = "LowMemoryUseCase";

    public static synchronized void onLowMemory() {
        MemoryCache.getInstance().clearAll();

    }

    @Override
    public String getName() {
        return NAME;
    }
}

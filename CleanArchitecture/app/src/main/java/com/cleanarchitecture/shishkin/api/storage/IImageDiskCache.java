package com.cleanarchitecture.shishkin.api.storage;

import android.graphics.Bitmap;

/**
 * The interface Image disk cache.
 */
public interface IImageDiskCache {

    /**
     * Put bitmap to cache
     *
     * @param key    the key
     * @param bitmap the bitmap
     */
    void put(String key, Bitmap bitmap);

    /**
     * Put bitmap to cache with expired period
     *
     * @param key     the key
     * @param bitmap  the bitmap
     * @param expired the expired
     */
    void put(String key, Bitmap bitmap, long expired);

    /**
     * Get bitmap from cache
     *
     * @param key the key
     * @return the bitmap
     */
    Bitmap get(String key);

    /**
     * Remove bitmap from cache
     *
     * @param key the key
     */
    void clear(String key);

    /**
     * Clear all bitmaps
     */
    void clear();

    /**
     * Flush cache
     */
    void flush();

    /**
     * Close cache
     */
    void close();

}



package com.cleanarchitecture.shishkin.api.storage;

import android.graphics.Bitmap;

public interface IImageDiskCache {

    void put(String key, Bitmap bitmap);

    void put(String key, Bitmap bitmap, long expired);

    Bitmap get(String key);

    void clear(String key);

    void clear();

    void flush();

    void close();

}



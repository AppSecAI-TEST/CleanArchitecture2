package com.cleanarchitecture.shishkin.api.storage;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.PreferencesModule;
import com.cleanarchitecture.shishkin.api.controller.ApplicationController;
import com.cleanarchitecture.shishkin.api.controller.Constant;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.CloseUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("unused")
public class ImageDiskCache extends AbstractModule implements IImageDiskCache {
    public static final String NAME = ImageDiskCache.class.getName();
    private static final String LOG_TAG = "ImageDiskCache:";

    private static final int INDEX_EXPIRED = 0;
    private static final int INDEX_DATA = 1;
    private static final int COUNT_INDEX = 2;

    private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    private static final int COMPRESS_QUALITY = 100;
    private static final int DISK_CACHE_SIZE = Constant.MB * 100; // 100MB
    private static final int BUFFER_SIZE = Constant.KB * 16; // 16kb
    private static final String DISK_CACHE_DIR = ApplicationController.getInstance().getCachePath() + File.separator + "ImageDiskCache";

    private static volatile ImageDiskCache sInstance;
    private DiskLruCache mDiskLruCache;
    private ReentrantLock mLock;

    public static ImageDiskCache getInstance() {
        if (sInstance == null) {
            synchronized (ImageDiskCache.class) {
                if (sInstance == null) {
                    sInstance = new ImageDiskCache();
                }
            }
        }
        return sInstance;
    }

    private ImageDiskCache() {
        mLock = new ReentrantLock();

        init();
    }

    private void init() {
        if (!ApplicationUtils.checkPermission(ApplicationController.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return;
        }

        int version = PreferencesModule.getInstance().getImageCacheVersion();
        if (version == 0) {
            version = ApplicationController.getInstance().getVersion();
            PreferencesModule.getInstance().setImageCacheVersion(version);
        }

        mLock.lock();

        try {
            if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
                final File dir = new File(DISK_CACHE_DIR + File.separator + version);
                if (!dir.exists() && !dir.mkdirs()) {
                    return;
                }
                mDiskLruCache = DiskLruCache.open(dir, version, COUNT_INDEX, DISK_CACHE_SIZE);
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void put(final String key, final Bitmap bitmap) {
        put(key, bitmap, 0);
    }

    @Override
    public void put(final String key, final Bitmap bitmap, final long expired) {
        if (mDiskLruCache == null || StringUtils.isNullOrEmpty(key) || bitmap == null) {
            return;
        }

        final String hash = hashKeyForDisk(key);
        OutputStream out = null;
        DiskLruCache.Editor editor = null;
        DiskLruCache.Snapshot snapshot = null;

        mLock.lock();

        try {
            snapshot = mDiskLruCache.get(hash);
            if (snapshot == null) {
                editor = mDiskLruCache.edit(hash);
                if (editor != null) {
                    out = editor.newOutputStream(INDEX_EXPIRED);
                    out.write(String.valueOf(expired).getBytes(Charsets.UTF_8));
                    out.flush();
                    out.close();

                    out = new BufferedOutputStream(editor.newOutputStream(INDEX_DATA), BUFFER_SIZE);
                    bitmap.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, out);
                    out.flush();
                    out.close();

                    editor.commit();
                }
                mDiskLruCache.flush();
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
            if (editor != null) {
                try {
                    editor.abort();
                } catch (Exception e1) {
                    ErrorController.getInstance().onError(LOG_TAG, e1);
                }
            }
        } finally {
            CloseUtils.close(out);
            CloseUtils.close(snapshot);
            mLock.unlock();
        }
    }

    @Override
    public Bitmap get(final String key) {
        if (mDiskLruCache == null || StringUtils.isNullOrEmpty(key)) {
            return null;
        }

        final String hash = hashKeyForDisk(key);
        Bitmap bitmap = null;
        InputStream inputStream = null;
        BufferedInputStream buffInputStream = null;
        long expired = -1;
        DiskLruCache.Snapshot snapshot = null;

        mLock.lock();

        try {
            snapshot = mDiskLruCache.get(hash);
            if (snapshot != null) {
                inputStream = snapshot.getInputStream(INDEX_EXPIRED);
                if (inputStream != null) {
                    final String s = CharStreams.toString(new InputStreamReader(
                            inputStream, Charsets.UTF_8));
                    inputStream.close();
                    if (!StringUtils.isNullOrEmpty(s)) {
                        expired = StringUtils.toLong(s);
                    }
                }

                if (expired == 0 || expired >= System.currentTimeMillis()) {
                    inputStream = snapshot.getInputStream(INDEX_DATA);
                    if (inputStream != null) {
                        buffInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE);
                        bitmap = BitmapFactory.decodeStream(buffInputStream);
                        inputStream.close();
                        buffInputStream.close();
                    }
                } else {
                    snapshot.close();
                    mDiskLruCache.remove(hash);
                    mDiskLruCache.flush();
                }
            }
        } catch (final IOException e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            CloseUtils.close(inputStream);
            CloseUtils.close(buffInputStream);
            CloseUtils.close(snapshot);
            mLock.unlock();
        }
        return bitmap;
    }

    @Override
    public void clear(final String key) {
        if (mDiskLruCache == null || StringUtils.isNullOrEmpty(key)) {
            return;
        }

        final String hash = hashKeyForDisk(key);

        mLock.lock();

        try {
            final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(hash);
            if (snapshot != null) {
                snapshot.close();
                mDiskLruCache.remove(hash);
                mDiskLruCache.flush();
            }
        } catch (final IOException e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void clear() {
        if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
            mLock.lock();

            try {
                mDiskLruCache.delete();
            } catch (IOException e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            } finally {
                mLock.unlock();
            }
            mDiskLruCache = null;
            PreferencesModule.getInstance().setImageCacheVersion(0);

            init();
        }
    }

    @Override
    public void flush() {
        if (mDiskLruCache == null) {
            return;
        }

        mLock.lock();

        try {
            mDiskLruCache.flush();
        } catch (IOException e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void close() {
        if (mDiskLruCache == null) {
            return;
        }

        mLock.lock();

        try {
            if (!mDiskLruCache.isClosed()) {
                mDiskLruCache.close();
                mDiskLruCache = null;
            }
        } catch (IOException e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes(Charsets.UTF_8));
            cacheKey = StringUtils.byteArrayToHex(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
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
    public String getDescription() {
        return "Image disk cache";
    }
}

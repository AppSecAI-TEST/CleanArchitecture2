package com.cleanarchitecture.shishkin.api.storage;

import android.Manifest;
import android.os.Parcel;
import android.os.Parcelable;

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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ParcelableDiskCache<T extends Parcelable> extends AbstractModule implements IExpiredParcelableStorage<T> {
    public static final String NAME = ParcelableDiskCache.class.getName();
    private static final String LOG_TAG = "ParcelableDiskCache:";

    private static final int INDEX_EXPIRED = 0;
    private static final int INDEX_DATA = 1;
    private static final int COUNT_INDEX = 2;
    private static final String LIST = "LIST";
    private static final String PARCELABLE = "PARCELABLE";

    private static final String DISK_CACHE_DIR = ApplicationController.getInstance().getCachePath() + File.separator + "ParcelableDiskCache";
    private static final int DISK_CACHE_SIZE = Constant.MB * 10; // 10MB
    private static final int BUFFER_SIZE = Constant.KB * 16; // 16kb

    private static volatile ParcelableDiskCache sInstance;
    private DiskLruCache mDiskLruCache;
    private ReentrantLock mLock;

    public static ParcelableDiskCache getInstance() {
        if (sInstance == null) {
            synchronized (ParcelableDiskCache.class) {
                if (sInstance == null) {
                    sInstance = new ParcelableDiskCache();
                }
            }
        }
        return sInstance;
    }

    private ParcelableDiskCache() {
        mLock = new ReentrantLock();

        init();
    }

    private void init() {
        if (!ApplicationUtils.checkPermission(ApplicationController.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return;
        }

        int version = PreferencesModule.getInstance().getParcelableDiskCacheVersion();
        if (version == 0) {
            version = ApplicationController.getInstance().getVersion();
            PreferencesModule.getInstance().setParcelableDiskCacheVersion(version);
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
    public void put(final String key, final T value) {
        put(key, value, 0);
    }

    @Override
    public void put(final String key, final T value, final long expired) {
        if (mDiskLruCache == null || StringUtils.isNullOrEmpty(key) || value == null) {
            return;
        }

        mLock.lock();

        final Parcel parcel = Parcel.obtain();
        parcel.writeString(PARCELABLE);
        parcel.writeParcelable(value, 0);

        final String hash = hashKeyForDisk(key);
        OutputStream out = null;
        DiskLruCache.Editor editor = null;
        DiskLruCache.Snapshot snapshot = null;

        try {
            snapshot = mDiskLruCache.get(hash);
            if (snapshot == null) {
                editor = mDiskLruCache.edit(hash);
                if (editor != null) {
                    out = editor.newOutputStream(INDEX_EXPIRED);
                    writeToOutputStream(out, String.valueOf(expired).getBytes(Charsets.UTF_8));

                    out = new BufferedOutputStream(editor.newOutputStream(INDEX_DATA), BUFFER_SIZE);
                    writeToOutputStream(out, parcel.marshall());

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
            parcel.recycle();
            mLock.unlock();
        }
    }

    @Override
    public void put(final String key, final List<T> values) {
        put(key, values, 0);
    }

    @Override
    public void put(final String key, final List<T> values, final long expired) {
        if (mDiskLruCache == null || StringUtils.isNullOrEmpty(key) || values == null) {
            return;
        }

        mLock.lock();

        final Parcel parcel = Parcel.obtain();
        parcel.writeString(LIST);
        parcel.writeList(values);

        final String hash = hashKeyForDisk(key);
        OutputStream out = null;
        DiskLruCache.Editor editor = null;
        DiskLruCache.Snapshot snapshot = null;

        try {
            snapshot = mDiskLruCache.get(hash);
            if (snapshot == null) {
                editor = mDiskLruCache.edit(hash);
                if (editor != null) {
                    out = editor.newOutputStream(INDEX_EXPIRED);
                    writeToOutputStream(out, String.valueOf(expired).getBytes(Charsets.UTF_8));

                    out = new BufferedOutputStream(editor.newOutputStream(INDEX_DATA), BUFFER_SIZE);
                    writeToOutputStream(out, parcel.marshall());

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
            parcel.recycle();
            mLock.unlock();
        }
    }

    @Override
    public T get(final String key, final Class itemClass) {
        if (mDiskLruCache == null || StringUtils.isNullOrEmpty(key) || itemClass == null) {
            return null;
        }

        mLock.lock();

        final String hash = hashKeyForDisk(key);
        InputStream inputStream = null;
        long expired = -1;
        DiskLruCache.Snapshot snapshot = null;
        final Parcel parcel = Parcel.obtain();

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
                        final byte[] value = getFromInputStream(inputStream);
                        inputStream.close();
                        if (value != null) {
                            parcel.unmarshall(value, 0, value.length);
                            parcel.setDataPosition(0);
                            final String type = parcel.readString();
                            if (PARCELABLE.equals(type)) {
                                return parcel.readParcelable(itemClass.getClassLoader());
                            }
                        }
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
            CloseUtils.close(snapshot);
            parcel.recycle();
            mLock.unlock();
        }
        return null;
    }

    @Override
    public List<T> getList(final String key, final Class itemClass) {
        if (mDiskLruCache == null || StringUtils.isNullOrEmpty(key) || itemClass == null) {
            return null;
        }

        mLock.lock();

        final String hash = hashKeyForDisk(key);
        InputStream inputStream = null;
        long expired = -1;
        DiskLruCache.Snapshot snapshot = null;
        final Parcel parcel = Parcel.obtain();

        try {
            snapshot = mDiskLruCache.get(hash);
            if (snapshot != null) {
                inputStream = snapshot.getInputStream(INDEX_EXPIRED);
                if (inputStream != null) {
                    String s = CharStreams.toString(new InputStreamReader(
                            inputStream, Charsets.UTF_8));
                    inputStream.close();
                    if (!StringUtils.isNullOrEmpty(s)) {
                        expired = StringUtils.toLong(s);
                    }
                }

                if (expired == 0 || expired >= System.currentTimeMillis()) {
                    inputStream = snapshot.getInputStream(INDEX_DATA);
                    if (inputStream != null) {
                        final byte[] value = getFromInputStream(inputStream);
                        inputStream.close();
                        if (value != null) {
                            parcel.unmarshall(value, 0, value.length);
                            parcel.setDataPosition(0);
                            final String type = parcel.readString();
                            if (LIST.equals(type)) {
                                final ArrayList<T> res = new ArrayList<>();
                                parcel.readList(res, itemClass.getClassLoader());
                                return res;
                            }
                        }
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
            CloseUtils.close(snapshot);
            parcel.recycle();
            mLock.unlock();
        }
        return null;
    }

    private void writeToOutputStream(final OutputStream stream, final byte[] array) throws IOException {
        stream.write(array);
        stream.flush();
        stream.close();
    }

    private byte[] getFromInputStream(final InputStream is) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            final byte[] data = new byte[1024];
            int count;
            while ((count = is.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, count);
            }
            outputStream.flush();
            return outputStream.toByteArray();
        } finally {
            CloseUtils.close(is);
            CloseUtils.close(outputStream);
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
    public void clear(final String key) {
        if (mDiskLruCache == null || StringUtils.isNullOrEmpty(key)) {
            return;
        }

        mLock.lock();

        final String hash = hashKeyForDisk(key);
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
            PreferencesModule.getInstance().setParcelableDiskCacheVersion(0);

            init();
        }
    }

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
        return "Parcelable disk cache";
    }
}

package com.cleanarchitecture.shishkin.api.storage;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cleanarchitecture.shishkin.api.controller.AbstractModule;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.AppPreferences;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.app.Constant;
import com.cleanarchitecture.shishkin.common.utils.CloseUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.locks.ReentrantLock;

public class ImageCache extends AbstractModule implements IImageCache {
    public static final String NAME = ImageCache.class.getName();
    private static final String LOG_TAG = "ImageCache:";

    private static final int INDEX_EXPIRED = 0;
    private static final int INDEX_DATA = 1;
    private static final int COUNT_INDEX = 2;

    private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    private static final int COMPRESS_QUALITY = 100;
    private static final int DISK_CACHE_SIZE = Constant.MB * 50; // 50MB
    private static final String DISK_CACHE_DIR = ApplicationController.EXTERNAL_STORAGE_APPLICATION_PATH + File.separator + "ImageCache";

    private static volatile ImageCache sInstance;
    private DiskLruCache mDiskLruCache;
    private ReentrantLock mLock;

    private int mVersion = 0;

    public static ImageCache getInstance() {
        if (sInstance == null) {
            synchronized (ImageCache.class) {
                if (sInstance == null) {
                    sInstance = new ImageCache();
                }
            }
        }
        return sInstance;
    }

    private ImageCache() {
        mLock = new ReentrantLock();

        init();
    }

    private void init() {
        if (!AdminUtils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return;
        }

        final Context context = AdminUtils.getContext();
        if (context != null) {
            mVersion = AppPreferences.getImageCacheVersion(context, mVersion);
        }

        mLock.lock();

        try {
            if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
                final File dir = new File(DISK_CACHE_DIR + File.separator + mVersion);
                if (!dir.exists() && !dir.mkdirs()) {
                    return;
                }
                mDiskLruCache = DiskLruCache.open(dir, mVersion, COUNT_INDEX, DISK_CACHE_SIZE);
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

        mLock.lock();

        final String hash = hashKeyForDisk(key);
        OutputStream out = null;
        DiskLruCache.Editor editor = null;

        try {
            final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(hash);
            if (snapshot == null) {
                editor = mDiskLruCache.edit(hash);
                if (editor != null) {
                    out = editor.newOutputStream(INDEX_EXPIRED);
                    out.write(String.valueOf(expired).getBytes());
                    out.flush();
                    out.close();

                    out = editor.newOutputStream(INDEX_DATA);
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
                }
            }
        } finally {
            CloseUtils.close(out);
            mLock.unlock();
        }
    }

    @Override
    public Bitmap get(final String key) {
        if (mDiskLruCache == null || StringUtils.isNullOrEmpty(key)) {
            return null;
        }

        mLock.lock();

        final String hash = hashKeyForDisk(key);
        Bitmap bitmap = null;
        InputStream inputStream = null;
        long expired = -1;

        try {
            final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(hash);
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
                        FileDescriptor fd = ((FileInputStream) inputStream).getFD();
                        bitmap = decodeSampledBitmapFromDescriptor(fd, Integer.MAX_VALUE, Integer.MAX_VALUE);
                        inputStream.close();
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
            mLock.unlock();
        }
        return bitmap;
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
        clearCache();

        init();
    }

    private void clearCache() {
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
        }
    }

    @Override
    public void setVersion(final int version) {
        final Context context = AdminUtils.getContext();
        if (context != null && version > mVersion) {
            clearCache();

            mVersion = version;
            AppPreferences.setImageCacheVersion(context, mVersion);

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

    private Bitmap decodeSampledBitmapFromDescriptor(
            FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2L;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
    }


    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
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
}

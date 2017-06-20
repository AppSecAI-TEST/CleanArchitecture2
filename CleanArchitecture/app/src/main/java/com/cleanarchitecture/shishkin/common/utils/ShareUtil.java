package com.cleanarchitecture.shishkin.common.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.github.snowdream.android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

public class ShareUtil {

    public static void performShare(final ShareData shareData, final Activity activity, final Uri uri) {
        try {
            if (activity != null && shareData != null && (!(activity.isFinishing()))) {
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");

                final List<LabeledIntent> targetedShareIntents = new ArrayList<>();

                final PackageManager pm = activity.getApplicationContext().getPackageManager();
                final List<ResolveInfo> activityList =
                        pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (final ResolveInfo app : activityList) {
                    final String packageName = app.activityInfo.packageName;
                    final String className = app.activityInfo.name;

                    if (packageName.equalsIgnoreCase(activity.getPackageName())) {
                        continue;
                    }

                    final Intent dummyIntent = new Intent();
                    dummyIntent.setAction(Intent.ACTION_SEND);
                    dummyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    dummyIntent.setComponent(new ComponentName(packageName, className));

                    final LabeledIntent targetedShareIntent = new LabeledIntent(dummyIntent, packageName, app.loadLabel(pm), app.icon);

                    if (packageName.equalsIgnoreCase("com.whatsapp")) {
                        if (uri != null) {
                            targetedShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            targetedShareIntent.setType("image/jpg");
                        } else {
                            targetedShareIntent.setType("text/plain");
                        }

                        if (!StringUtils.isNullOrEmpty(shareData.getTitle())) {
                            targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, shareData.getTitle());
                        }
                        targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareData.getMessage());
                        targetedShareIntents.add(0, targetedShareIntent);
                    } else if (packageName.equalsIgnoreCase("com.facebook.orca")
                            || packageName.equalsIgnoreCase("com.facebook.katana")) {

                        if (!StringUtils.isNullOrEmpty(shareData.getTitle())) {
                            targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareData.getTitle() + "\n" + shareData.getMessage());
                        } else {
                            targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareData.getMessage());
                        }
                        targetedShareIntent.setType("text/plain");
                        targetedShareIntents.add(0, targetedShareIntent);
                    } else if (packageName.equalsIgnoreCase("com.android.mms")
                            || (packageName.equalsIgnoreCase("com.google.android.talk"))) {
                        if (uri != null) {
                            targetedShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            targetedShareIntent.setType("image/jpg");
                        } else {
                            targetedShareIntent.setType("text/plain");
                        }
                        if (!StringUtils.isNullOrEmpty(shareData.getTitle())) {
                            targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareData.getTitle() + "\n" + shareData.getMessage());
                        } else {
                            targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareData.getMessage());
                        }
                        targetedShareIntents.add(0, targetedShareIntent);
                    } else if (packageName.equalsIgnoreCase("com.google.android.gm")) {
                        if (uri != null) {
                            targetedShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            targetedShareIntent.setType("image/jpg");
                        } else {
                            targetedShareIntent.setType("text/plain");
                        }
                        if (!StringUtils.isNullOrEmpty(shareData.getTitle())) {
                            targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, shareData.getTitle());
                        }
                        targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareData.getMessage());
                        targetedShareIntents.add(0, targetedShareIntent);
                    } else if (packageName.equalsIgnoreCase("com.twitter.android")
                            || packageName.equalsIgnoreCase("com.twidroid")
                            || packageName.equalsIgnoreCase("com.handmark.tweetcaster")
                            || packageName.equalsIgnoreCase("com.thedeck.android")) {
                        targetedShareIntent.setType("text/plain");
                        targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareData.getMessage());
                        targetedShareIntents.add(0, targetedShareIntent);
                    } else if (packageName.equalsIgnoreCase("org.telegram.messenger")) {
                        if (uri != null) {
                            targetedShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            targetedShareIntent.setType("image/jpg");
                        } else {
                            targetedShareIntent.setType("text/plain");
                        }
                        targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareData.getMessage());
                        targetedShareIntents.add(0, targetedShareIntent);
                    } else if (packageName.equalsIgnoreCase("com.viber.voip")) {
                        if (uri != null) {
                            targetedShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            targetedShareIntent.setType("image/jpg");
                        } else {
                            targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareData.getMessage());
                            targetedShareIntent.setType("text/plain");
                        }
                        targetedShareIntents.add(0, targetedShareIntent);
                    } else {
                        if (uri != null) {
                            targetedShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            targetedShareIntent.setType("image/jpg");
                        } else {
                            targetedShareIntent.setType("text/plain");
                        }
                        if (!StringUtils.isNullOrEmpty(shareData.getTitle())) {
                            targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, shareData.getTitle());
                        }
                        targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareData.getMessage());
                        targetedShareIntents.add(targetedShareIntent);
                    }
                }

                final Intent chooserIntent =
                        Intent.createChooser((targetedShareIntents.remove(targetedShareIntents.size() - 1)),
                                activity.getString(R.string.share_with));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                        targetedShareIntents.toArray(new Parcelable[]{}));
                activity.startActivity(chooserIntent);
            }
        } catch (Exception e) {
            Log.e("ShareUtil", e.getMessage());
        }
    }

    public static class ShareData {
        private String mMessage;
        private String mTitle;

        public ShareData(final String title, final String message) {
            this.mTitle = title;
            this.mMessage = message;
        }

        public String getMessage() {
            return mMessage;
        }

        public String getTitle() {
            return mTitle;
        }
    }

    private static Bitmap takeScreenShot(@NonNull final Activity activity, final View myView) {
        Bitmap bitmap = null;
        try {
            View view;
            if (myView != null) {
                view = myView;
            } else {
                view = activity.getWindow().getDecorView();
            }
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            final Bitmap bitmap1 = view.getDrawingCache();
            if (myView != null) {
                bitmap =
                        Bitmap.createBitmap(bitmap1, 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                Bitmap bitmap2 =
                        BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
                bitmap = combineImages(bitmap, bitmap2);
            } else {
                final Rect frame = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                int statusBarHeight = frame.top;
                int width = activity.getWindowManager().getDefaultDisplay().getWidth();
                int height = activity.getWindowManager().getDefaultDisplay().getHeight();
                bitmap = Bitmap.createBitmap(bitmap1, 0, statusBarHeight, width, height - statusBarHeight);
            }
            view.destroyDrawingCache();
        } catch (Exception e) {
            Log.e("ShareUtil", e.getMessage());
        }
        return bitmap;
    }

    private static boolean savePic(final Bitmap bitmap, final File strFileName) {
        FileOutputStream fos = null;
        boolean isFileSaved = false;
        try {
            fos = new FileOutputStream(strFileName);
            if (fos != null && bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                isFileSaved = true;
            }
        } catch (Exception e) {
            isFileSaved = false;
            Log.e("ShareUtil", e.getMessage());
        } finally {
            CloseUtils.close(fos);
        }
        return isFileSaved;
    }

    public static void performShareWithImage(final ShareData shareData, final Activity activity, final View myView) {
        if (ApplicationUtils.checkPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) && ApplicationUtils.checkPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String file_path =
                    Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + activity.getPackageName();
            try {
                final File dir = new File(file_path);
                if (!dir.exists()) dir.mkdirs();
                final File file = new File(dir, "product.jpg");
                final Uri uri = Uri.fromFile(file);
                final boolean isPicSaved = savePic(takeScreenShot(activity, myView), file);
                if (!isPicSaved) {
                    performShare(shareData, activity, null);
                } else {
                    performShare(shareData, activity, uri);
                }
            } catch (Exception e) {
                Log.e("ShareUtil", e.getMessage());
            }
        }
    }

    private static Bitmap combineImages(final Bitmap c, final Bitmap s) {
        Bitmap cs = null;

        int width = c.getWidth(), height = c.getHeight();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth() - s.getWidth(), 0f, null);

        return cs;
    }
}

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

public class ShareUtil {

    private ShareUtil() {
    }

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

    public static void performShareWithImage(final ShareData shareData, final Activity activity, final String imageFileName) {
        if (StringUtils.isNullOrEmpty(imageFileName)) {
            return;
        }

        try {
            final File file = new File(imageFileName);
            if (file.exists()) {
                final Uri uri = Uri.fromFile(file);
                performShare(shareData, activity, uri);
            }
        } catch (Exception e) {
            Log.e("ShareUtil", e.getMessage());
        }
    }

}

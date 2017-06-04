package com.cleanarchitecture.shishkin.base.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.snowdream.android.util.Log;

/**
 * {@code IntentUtils} contains static methods which operate with {@code Intent}.
 */
public class IntentUtils {

    private static final String LOG_TAG = "IntentUtils";

    /**
     * Create {@link Intent} with action that will be sent to a given component.
     *
     * @param context The context
     * @param cls     The class to be used as target.
     * @param action  The intent action.
     * @return The {@link Intent} with action
     */
    @NonNull
    public static Intent createActionIntent(final Context context, final Class<?> cls,
                                            final String action) {
        final Intent intent = new Intent(context, cls);
        intent.setAction(action);
        return intent;
    }

    /**
     * Return true if activity that can handle a given intent action is found.
     *
     * @param context The context.
     * @param action  An intent action.
     * @return true if activity that can handle a given action is found, false otherwise.
     */
    public static boolean canStartActivity(@NonNull final Context context, @NonNull final String action) {
        return canStartActivity(context, new Intent(action));
    }

    /**
     * Return true if activity that can handle a given intent is found.
     *
     * @param context The context.
     * @param intent  An intent containing all of the desired specification
     *                (action, data, type, category, and/or component).
     * @return true if activity that can handle a given intent is found, false otherwise.
     */
    public static boolean canStartActivity(@NonNull final Context context, @NonNull final Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        return (packageManager != null && packageManager.resolveActivity(intent, 0) != null);
    }

    public static void openGooglePlayDeveloper(final Context context, final String developerName,
                                               final CharSequence dialogName) {
        final String googlePlayDeveloperUrl = "https://play.google.com/store/apps/developer?id=" + developerName;

        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://search?q=pub:" + developerName));
        if (canStartActivity(context, intent)) {
            context.startActivity(Intent.createChooser(intent, dialogName));
        } else {
            openWebBrowser(context, googlePlayDeveloperUrl);
        }
    }

    public static void openGooglePlay(final Context context, final String packageName) {
        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + packageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (canStartActivity(context, intent)) {
            context.startActivity(intent);
        } else {
            openWebBrowser(context, "http://play.google.com/store/apps/details?id=" + packageName);
        }
    }

    public static void openWebBrowser(final Context context, @Nullable final String url) {
        if (TextUtilsExt.isEmpty(url)) {
            return;
        }

        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (canStartActivity(context, intent)) {
            context.startActivity(intent);
        }
    }

    public static Intent getUninstallAppIntent(String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getLaunchAppIntent(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }

    public static Intent getAppDetailsSettingsIntent(String packageName) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getShareTextIntent(String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        return intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getShareImageIntent(String content, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/*");
        return intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getComponentIntent(String packageName, String className) {
        return getComponentIntent(packageName, className, null);
    }

    public static Intent getComponentIntent(String packageName, String className, Bundle bundle) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (bundle != null) intent.putExtras(bundle);
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getShutdownIntent() {
        Intent intent = new Intent(Intent.ACTION_SHUTDOWN);
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent getCaptureIntent(Uri outUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        return intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    private IntentUtils() {
    }

}

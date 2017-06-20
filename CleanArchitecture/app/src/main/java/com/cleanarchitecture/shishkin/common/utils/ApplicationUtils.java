package com.cleanarchitecture.shishkin.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ApplicationUtils {

    private static final String LOG_TAG = "ApplicationUtils:";
    public static final int REQUEST_PERMISSIONS = 10000;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 10001;

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public static boolean hasJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP ||
                Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean hasN() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean hasNMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1;
    }

    public static boolean hasO() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1;
    }

    public static boolean hasNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static String getPhoneInfo() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("Android version : " + Build.VERSION.RELEASE);
        sb.append("\n");
        sb.append("Board:" + Build.BOARD);
        sb.append("\n");
        sb.append("Manufacturer:" + Build.MANUFACTURER);
        sb.append("\n");
        sb.append("Model:" + Build.MODEL);
        sb.append("\n");
        sb.append("Product:" + Build.PRODUCT);
        sb.append("\n");
        sb.append("Device:" + Build.DEVICE);
        sb.append("\n");
        sb.append("ROM:" + Build.DISPLAY);
        sb.append("\n");
        sb.append("Hardware:" + Build.HARDWARE);
        sb.append("\n");
        sb.append("Id:" + Build.ID);
        sb.append("\n");
        sb.append("Tags:" + Build.TAGS);
        sb.append("\n");
        return sb.toString();
    }

    public static void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }

    public static int getStatusPermission(final Context context, final String permission) {
        if (context != null) {
            if (ApplicationUtils.hasMarshmallow()) {
                return ActivityCompat.checkSelfPermission(context, permission);
            }
            return PackageManager.PERMISSION_GRANTED;
        } else {
            return PackageManager.PERMISSION_DENIED;
        }
    }

    public static boolean checkPermission(final Context context, final String permission) {
        return getStatusPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean grantPermisions(final String[] permissions, final Activity activity) {
        if (activity != null && permissions != null) {
            if (hasMarshmallow()) {
                final List<String> listPermissionsNeeded = new ArrayList();

                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(),
                            permission)
                            != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(permission);
                    }
                }

                if (!listPermissionsNeeded.isEmpty()) {
                    String[] arrayPermissionsNeeded = new String[listPermissionsNeeded.size()];
                    listPermissionsNeeded.toArray(arrayPermissionsNeeded);
                    if (activity != null) {
                        ActivityCompat.requestPermissions(activity,
                                arrayPermissionsNeeded,
                                REQUEST_PERMISSIONS);
                    }
                    return false;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the handle to a system-level service by name. The class of the
     * returned object varies by the requested name.
     */
    public static <S> S getSystemService(final Context context, final String serviceName) {
        if (context != null) {
            return SafeUtils.cast(context.getSystemService(serviceName));
        }
        return null;
    }

    public static int getResourceId(final Context context, final String typeResource, final String nameResource) {
        // Example: context.getResources().getIdentifier("widget_blue", "layout", context.getPackageName())
        if (context != null) {
            return context.getResources().getIdentifier(nameResource, typeResource, context.getPackageName());
        }
        return 0;
    }

    private ApplicationUtils() {
    }

}

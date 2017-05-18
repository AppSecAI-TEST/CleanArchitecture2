package com.cleanarchitecture.shishkin.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.github.snowdream.android.util.Log;

public class ApplicationUtils {

    private static final String LOG_TAG = "ApplicationUtils:";

    public static final int REQ_GOOGLE_PLAY_SERVICES = 10001;
    public static final int REQ_PERMISSIONS = 1;

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

    public static boolean hasNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    // It is the only one method to change built-in colors and yes - it's a hack
    private static void brandGlowDrawableColor(final Context context, final String drawable, final int color) {
        final Resources resources = context.getResources();
        final int drawableRes = resources.getIdentifier(drawable, "drawable", "android");
        try {
            final Drawable d = ViewUtils.getDrawable(context, drawableRes);
            if (d != null) {
                d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        } catch (final Resources.NotFoundException rnfe) {
            Log.e(LOG_TAG, "Failed to find drawable for resource " + drawable, rnfe);
        }
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

    /**
     * Return the handle to a system-level service by name. The class of the
     * returned object varies by the requested name.
     */
    public  static <S> S getSystemService(final Context context, final String serviceName) {
        if (context != null) {
            return SafeUtils.cast(context.getSystemService(serviceName));
        }
        return null;
    }

    public static  void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }

    public static void isIgnoringBatteryOptimizations(final Activity activity) {
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
                if (pm != null) {
                    if (!pm.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID)) {
                        final Intent myIntent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        activity.startActivity(myIntent);
                    }
                }
            }
        }
    }

    public static void canDrawOverlays(final Activity activity) {
        if (activity != null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (!Settings.canDrawOverlays(activity)) {
                    final Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    activity.startActivity(myIntent);
                }
            }
        }
    }

    private ApplicationUtils() {
    }

}

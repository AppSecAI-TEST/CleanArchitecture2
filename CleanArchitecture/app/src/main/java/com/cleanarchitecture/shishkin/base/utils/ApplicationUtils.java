package com.cleanarchitecture.shishkin.base.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.base.controller.Admin;
import com.cleanarchitecture.shishkin.base.controller.ErrorController;
import com.cleanarchitecture.shishkin.base.controller.EventBusController;
import com.cleanarchitecture.shishkin.base.controller.ILifecycleController;
import com.cleanarchitecture.shishkin.base.controller.IMailController;
import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.controller.LifecycleController;
import com.cleanarchitecture.shishkin.base.controller.MailController;
import com.cleanarchitecture.shishkin.base.event.IEvent;
import com.cleanarchitecture.shishkin.base.mail.IMail;
import com.cleanarchitecture.shishkin.base.repository.IRepository;
import com.cleanarchitecture.shishkin.base.repository.Repository;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.base.ui.activity.AbstractContentActivity;
import com.cleanarchitecture.shishkin.base.ui.fragment.AbstractContentFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
            ErrorController.getInstance().onError(LOG_TAG, rnfe);
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
    public static <S> S getSystemService(final Context context, final String serviceName) {
        if (context != null) {
            return SafeUtils.cast(context.getSystemService(serviceName));
        }
        return null;
    }

    public static void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }

    public static void isIgnoringBatteryOptimizations(final Activity activity) {
        if (activity != null) {
            if (hasMarshmallow()) {
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

    public static void checkGooglePlayServices(final Activity activity) {
        if (activity != null) {
            final GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
            final int result = googleAPI.isGooglePlayServicesAvailable(activity);
            if (result != ConnectionResult.SUCCESS) {
                if (googleAPI.isUserResolvableError(result)) {
                    runOnUiThread(() -> {
                        final Dialog dialog = googleAPI.getErrorDialog(activity, result, ApplicationUtils.REQUEST_GOOGLE_PLAY_SERVICES);
                        dialog.setOnCancelListener(dialogInterface -> activity.finish());
                        dialog.show();
                    });
                }
            }
        }
    }

    public static void canDrawOverlays(final Activity activity) {
        if (activity != null) {
            if (hasMarshmallow()) {
                if (!Settings.canDrawOverlays(activity)) {
                    final Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    activity.startActivity(myIntent);
                }
            }
        }
    }

    public static boolean grantPermisions(final String[] permissions, final Activity activity) {
        if (hasMarshmallow()) {
            if (ApplicationController.getInstance() != null) {
                final List<String> listPermissionsNeeded = new ArrayList();

                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(ApplicationController.getInstance(),
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
                return false;
            }
        }
        return true;
    }

    public static int getStatusPermission(final String permission) {
        if (hasMarshmallow()) {
            if (ApplicationController.getInstance() != null) {
                return ActivityCompat.checkSelfPermission(ApplicationController.getInstance(), permission);
            } else {
                return PackageManager.PERMISSION_DENIED;
            }
        }
        return PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkPermission(final String permission) {
        return getStatusPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean existsDb(final Context context, final String nameDb) {
        try {
            final String pathDb = context.getDatabasePath(nameDb).getAbsolutePath();
            if (StringUtils.isNullOrEmpty(pathDb)) {
                return false;
            }

            final File file = new File(pathDb);
            if (file.exists()) {
                final long length = file.length();
                if (length > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
        return false;
    }

    public static void readMail(final IMailSubscriber subscriber) {
        final IMailController controller = Admin.getInstance().getModule(MailController.NAME);
        if (controller != null) {
            final List<IMail> list = controller.getMail(subscriber);
            for (IMail mail : list) {
                mail.read(subscriber);
                controller.removeMail(mail);
            }
        }
    }

    public static void addMail(final IMail mail) {
        final IMailController controller = Admin.getInstance().getModule(MailController.NAME);
        if (controller != null) {
            addMail(mail);
        }
    }

    public static AbstractActivity getActivity() {
        final ILifecycleController controller = Admin.getInstance().getModule(LifecycleController.NAME);
        if (controller != null) {
            return controller.getActivity();
        }
        return null;
    }

    public static AbstractContentFragment getContentFragment() {
        final ILifecycleController controller = Admin.getInstance().getModule(LifecycleController.NAME);
        if (controller != null) {
            final AbstractContentActivity activity = controller.getContentActivity();
            if (activity != null) {
                return activity.getContentFragment(AbstractContentFragment.class);
            }
        }
        return null;
    }

    public static void postEvent(IEvent event) {
        EventBusController.getInstance().post(event);
    }

    public static void postStickyEvent(IEvent event) {
        EventBusController.getInstance().postSticky(event);
    }

    public static void removeStickyEvent(IEvent event) {
        EventBusController.getInstance().removeSticky(event);
    }

    public static IRepository getRepository() {
        return Admin.getInstance().getModule(Repository.NAME);
    }



    private ApplicationUtils() {
    }

}

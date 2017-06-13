package com.cleanarchitecture.shishkin.api.controller;

import android.app.Dialog;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.api.event.IEvent;
import com.cleanarchitecture.shishkin.api.mail.IMail;
import com.cleanarchitecture.shishkin.api.repository.ContentProvider;
import com.cleanarchitecture.shishkin.api.repository.DbProvider;
import com.cleanarchitecture.shishkin.api.repository.IContentProvider;
import com.cleanarchitecture.shishkin.api.repository.IDbProvider;
import com.cleanarchitecture.shishkin.api.repository.INetProvider;
import com.cleanarchitecture.shishkin.api.repository.IRepository;
import com.cleanarchitecture.shishkin.api.repository.NetProvider;
import com.cleanarchitecture.shishkin.api.repository.Repository;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractActivity;
import com.cleanarchitecture.shishkin.api.ui.activity.AbstractContentActivity;
import com.cleanarchitecture.shishkin.api.ui.fragment.AbstractContentFragment;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.SafeUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.util.List;

@SuppressWarnings("unused")
public class AdminUtils {

    private static final String LOG_TAG = "AdminUtils:";
    public static final int REQUEST_PERMISSIONS = 10000;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 10001;

    /**
     * Получить системный сервис
     *
     * @param <S>         тип сервиса
     * @param serviceName имя сервмса
     * @return системный сервис
     */
    public static <S> S getSystemService(final String serviceName) {
        final Context context = getContext();
        if (context != null) {
            return SafeUtils.cast(context.getSystemService(serviceName));
        }
        return null;
    }

    /**
     * Добавить в список приложений, которые будут игнорироваться системой оптимизации питания
     */
    public static void isIgnoringBatteryOptimizations() {
        final ILifecycleController controller = Admin.getInstance().get(LifecycleController.NAME);
        if (controller != null) {
            final AbstractActivity activity = controller.getCurrentActivity();
            if (activity != null) {
                if (ApplicationUtils.hasMarshmallow()) {
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
    }

    /**
     * Контролировать наличие и версию Google Play Services
     */
    public static void checkGooglePlayServices() {
        final ILifecycleController controller = Admin.getInstance().get(LifecycleController.NAME);
        if (controller != null) {
            final AbstractActivity activity = controller.getCurrentActivity();
            if (activity != null) {
                final GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
                final int result = googleAPI.isGooglePlayServicesAvailable(activity);
                if (result != ConnectionResult.SUCCESS) {
                    if (googleAPI.isUserResolvableError(result)) {
                        ApplicationUtils.runOnUiThread(() -> {
                            final Dialog dialog = googleAPI.getErrorDialog(activity, result, AdminUtils.REQUEST_GOOGLE_PLAY_SERVICES);
                            dialog.setOnCancelListener(dialogInterface -> activity.finish());
                            dialog.show();
                        });
                    }
                }
            }
        }
    }

    /**
     * Добавить в список приложений, которым разрешен вывод поверх других окон
     */
    public static void canDrawOverlays() {
        final ILifecycleController controller = Admin.getInstance().get(LifecycleController.NAME);
        if (controller != null) {
            final AbstractActivity activity = controller.getCurrentActivity();
            if (activity != null) {
                if (ApplicationUtils.hasMarshmallow()) {
                    if (!Settings.canDrawOverlays(activity)) {
                        final Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        activity.startActivity(myIntent);
                    }
                }
            }
        }
    }

    /**
     * Запросить право приложению
     *
     * @param permission  наименование права
     * @param helpMessage текст сообщения, в случае если право запрещено пользователем
     */
    public static void grantPermission(final String permission, final String helpMessage) {
        if (ApplicationUtils.hasMarshmallow()) {
            final IActivityController controller = Admin.getInstance().get(ActivityController.NAME);
            if (controller != null) {
                controller.grantPermission(permission, helpMessage);
            }
        }
    }

    /**
     * Получить статус права приложения
     *
     * @param permission наименование права
     * @return статус права
     */
    public static int getStatusPermission(final String permission) {
        final Context context = getContext();
        if (context != null) {
            if (ApplicationUtils.hasMarshmallow()) {
                return ActivityCompat.checkSelfPermission(context, permission);
            }
            return PackageManager.PERMISSION_GRANTED;
        } else {
            return PackageManager.PERMISSION_DENIED;
        }
    }

    /**
     * Проверить разрешение права приложения
     *
     * @param permission наименование права
     * @return true - право приложению разрешено
     */
    public static boolean checkPermission(final String permission) {
        return getStatusPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Проверить наличие БД
     *
     * @param nameDb имя БД
     * @return true - БД существует
     */
    public static boolean existsDb(final String nameDb) {
        final Context context = getContext();
        if (context != null) {
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
        }
        return false;
    }

    /**
     * Читать почту
     *
     * @param subscriber почтовый подписчик
     */
    public static void readMail(final IMailSubscriber subscriber) {
        final IMailController controller = Admin.getInstance().get(MailController.NAME);
        if (controller != null) {
            final List<IMail> list = controller.getMail(subscriber);
            for (IMail mail : list) {
                mail.read(subscriber);
                controller.removeMail(mail);
            }
        }
    }

    /**
     * Добавить почтовое сообщение
     *
     * @param mail почтовое сообщение
     */
    public static void addMail(final IMail mail) {
        final IMailController controller = Admin.getInstance().get(MailController.NAME);
        if (controller != null) {
            addMail(mail);
        }
    }

    /**
     * Получить activity.
     *
     * @return the activity
     */
    public static AbstractActivity getActivity() {
        final ILifecycleController controller = Admin.getInstance().get(LifecycleController.NAME);
        if (controller != null) {
            return controller.getActivity();
        }
        return null;
    }

    /**
     * Получить текущую активную(onResume) activity
     *
     * @return the current activity
     */
    public static AbstractActivity getCurrentActivity() {
        final ILifecycleController controller = Admin.getInstance().get(LifecycleController.NAME);
        if (controller != null) {
            return controller.getCurrentActivity();
        }
        return null;
    }

    /**
     * Получить content fragment.
     *
     * @return the content fragment
     */
    public static AbstractContentFragment getContentFragment() {
        final ILifecycleController controller = Admin.getInstance().get(LifecycleController.NAME);
        if (controller != null) {
            final AbstractContentActivity activity = controller.getCurrentContentActivity();
            if (activity != null) {
                return activity.getContentFragment(AbstractContentFragment.class);
            }
        }
        return null;
    }

    /**
     * Получить context приложения
     *
     * @return the context
     */
    public static Context getContext() {
        return ApplicationController.getInstance();
    }

    /**
     * Послать event.
     *
     * @param event the event
     */
    public static void postEvent(final IEvent event) {
        EventBusController.getInstance().post(event);
    }

    /**
     * Послать sticky(постоянный) event.
     *
     * @param event the event
     */
    public static void postStickyEvent(final IEvent event) {
        EventBusController.getInstance().postSticky(event);
    }

    /**
     * Удалить sticky(постоянный) event.
     *
     * @param event the event
     */
    public static void removeStickyEvent(final IEvent event) {
        EventBusController.getInstance().removeSticky(event);
    }

    /**
     * Получить репозиторий
     *
     * @return репозиторий
     */
    public static IRepository getRepository() {
        return Admin.getInstance().get(Repository.NAME);
    }

    /**
     * Получить content провайдера
     *
     * @return content провайдер
     */
    public static IContentProvider getContentProvider() {
        return Admin.getInstance().get(ContentProvider.NAME);
    }

    /**
     * Получить провайдера БД
     *
     * @return провайдер БД
     */
    public static IDbProvider getDbProvider() {
        return Admin.getInstance().get(DbProvider.NAME);
    }

    /**
     * Получить сетевого провайдера
     *
     * @return сетевой провайдер
     */
    public static INetProvider getNetProvider() {
        return Admin.getInstance().get(NetProvider.NAME);
    }

    /**
     * Получить БД
     *
     * @param <T>          тип БД
     * @param klass        class БД
     * @param databaseName имя БД
     * @return the db
     */
    public static <T extends RoomDatabase> T getDb(final Class<T> klass, final String databaseName) {
        final IDbProvider provider = getDbProvider();
        if (provider != null) {
            return provider.getDb(klass, databaseName);
        }
        return null;
    }

    /**
     * Установить цвет status bar телефона у текущей activity
     *
     * @param color_res цвет
     */
    public static void setStatusBarColor(final int color_res) {
        final AbstractActivity activity = getCurrentActivity();
        if (activity != null) {
            ViewUtils.setStatusBarColor(activity, color_res);
        }
    }

    private AdminUtils() {
    }

}

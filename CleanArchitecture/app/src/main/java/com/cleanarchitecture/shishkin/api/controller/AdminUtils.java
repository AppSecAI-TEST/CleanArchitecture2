package com.cleanarchitecture.shishkin.api.controller;

import android.Manifest;
import android.app.Dialog;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.IEvent;
import com.cleanarchitecture.shishkin.api.mail.IMail;
import com.cleanarchitecture.shishkin.api.presenter.IPresenter;
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
import com.cleanarchitecture.shishkin.common.state.ViewStateObserver;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.IntentUtils;
import com.cleanarchitecture.shishkin.common.utils.SafeUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.common.io.Files;

import java.io.File;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

@SuppressWarnings("unused")
public class AdminUtils {

    private static final String LOG_TAG = "AdminUtils:";
    public static final int REQUEST_PERMISSIONS = 10000;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 10001;

    /**
     * Зарегистрировать подписчика модуля
     *
     * @param subscriber подписчик модуля
     */
    public static void register(final IModuleSubscriber subscriber) {
        Admin.getInstance().register(subscriber);
    }

    /**
     * Отменить регистрацию подписчика модуля
     *
     * @param subscriber подписчик модуля
     */
    public static void unregister(final IModuleSubscriber subscriber) {
        Admin.getInstance().unregister(subscriber);
    }

    /**
     * Получить системный сервис
     *
     * @param <S>         тип сервиса
     * @param serviceName имя сервмса
     * @return системный сервис
     */
    public static <S> S getSystemService(final String serviceName) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            return SafeUtils.cast(context.getSystemService(serviceName));
        }
        return null;
    }

    /**
     * Добавить в список приложений, которые будут игнорироваться системой оптимизации питания
     */
    public static void isIgnoringBatteryOptimizations() {
        final AbstractActivity activity = getActivity();
        if (activity != null) {
            if (ApplicationUtils.hasMarshmallow()) {
                final PowerManager pm = getSystemService(Context.POWER_SERVICE);
                if (pm != null && !pm.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID)) {
                    final Intent myIntent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    activity.startActivity(myIntent);
                }
            }
        }
    }

    /**
     * Контролировать наличие и версию Google Play Services, с показом диалога обновления
     */
    public static void checkGooglePlayServices() {
        final AbstractActivity activity = getActivity();
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

    /**
     * Контролировать наличие и версию Google Play Services
     */
    public static boolean isGooglePlayServices() {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context != null) {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
            if (ConnectionResult.SUCCESS == resultCode) {
                return true;
            }
        }
        return false;
    }


    /**
     * Добавить в список приложений, которым разрешен вывод поверх других окон
     */
    public static void canDrawOverlays() {
        final AbstractActivity activity = getActivity();
        if (activity != null) {
            if (ApplicationUtils.hasMarshmallow() && !Settings.canDrawOverlays(activity)) {
                final Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                activity.startActivity(myIntent);
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
        final Context context = ApplicationController.getInstance().getApplicationContext();
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
        final Context context = ApplicationController.getInstance().getApplicationContext();
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
                if (subscriber.getState() == ViewStateObserver.STATE_RESUME) {
                    mail.read(subscriber);
                    controller.removeMail(mail);
                }
            }
        }
    }

    /**
     * Читать почту не удаляя c сервера
     *
     * @param subscriber почтовый подписчик
     */
    public static void viewMail(final IMailSubscriber subscriber) {
        final IMailController controller = Admin.getInstance().get(MailController.NAME);
        if (controller != null) {
            final List<IMail> list = controller.getMail(subscriber);
            for (IMail mail : list) {
                if (subscriber.getState() == ViewStateObserver.STATE_RESUME) {
                    mail.read(subscriber);
                }
            }
        }
    }

    /**
     * Очистить почту подписчика
     *
     * @param subscriber почтовый подписчик
     */
    public static void clearMail(final IMailSubscriber subscriber) {
        final IMailController controller = Admin.getInstance().get(MailController.NAME);
        if (controller != null) {
            controller.clearMail(subscriber);
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
            controller.addMail(mail);
        }
    }

    /**
     * Получить activity.
     *
     * @return the activity
     */
    public static AbstractActivity getActivity() {
        final INavigationController controller = Admin.getInstance().get(NavigationController.NAME);
        if (controller != null) {
            return controller.getActivity();
        }
        return null;
    }

    /**
     * Получить activity.
     *
     * @param name имя activity
     * @return the activity
     */
    public static AbstractActivity getActivity(final String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            final INavigationController controller = Admin.getInstance().get(NavigationController.NAME);
            if (controller != null) {
                return controller.getActivity(name);
            }
        }
        return null;
    }

    /**
     * Стартовать activity.
     *
     * @param intent intent
     */
    public static void startActivity(final Intent intent) {
        if (intent != null) {
            final Context context = ApplicationController.getInstance().getApplicationContext();
            if (context != null) {
                context.startActivity(intent);
            }
        }
    }

    /**
     * Получить content fragment.
     *
     * @return the content fragment
     */
    public static AbstractContentFragment getContentFragment() {
        final INavigationController controller = Admin.getInstance().get(NavigationController.NAME);
        if (controller != null) {
            final AbstractContentActivity activity = controller.getContentActivity();
            if (activity != null) {
                return activity.getContentFragment(AbstractContentFragment.class);
            }
        }
        return null;
    }

    /**
     * Получить SwipeRefreshLayout
     *
     * @return SwipeRefreshLayout
     */
    public static SwipeRefreshLayout getSwipeRefreshLayout() {
        final AbstractContentFragment fragment = getContentFragment();
        if (fragment != null) {
            return fragment.getSwipeRefreshLayout();
        }
        return null;
    }

    /**
     * Получить context приложения
     *
     * @return the context
     */
    public static Context getContext() {
        return ApplicationController.getInstance().getApplicationContext();
    }

    /**
     * Получить prsenter
     *
     * @param name имя presenter
     * @return presenter
     */
    public static IPresenter getPresenter(final String name) {
        final PresenterController controller = Admin.getInstance().get(PresenterController.NAME);
        if (controller != null) {
            return controller.getPresenter(name);
        }
        return null;
    }

    /**
     * Получить prеsenter контроллер
     *
     * @return presenter контроллер
     */
    public static IPresenterController getPresenterController() {
        return Admin.getInstance().get(PresenterController.NAME);
    }

    /**
     * Получить сохраненное состояние prеsenter
     *
     * @return сохраненное состояние
     */
    public static Bundle getStateData(final String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            final IPresenterController controller = getPresenterController();
            if (controller != null) {
                return controller.restoreStateData(name);
            }
        }
        return null;
    }

    /**
     * Очистить сохраненное состояние prеsenter
     */
    public static void clearStateData(final String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            final IPresenterController controller = getPresenterController();
            if (controller != null) {
                controller.clearStateData(name);
            }
        }
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
     * Получить layout id рабочего стола
     *
     * @param name      имя layout
     * @param defaultId id ресурса по умолчанию
     * @return layout id
     */
    public static int getLayoutId(String name, int defaultId) {
        final IDesktopController module = Admin.getInstance().get(DesktopController.NAME);
        if (module != null) {
            return module.getLayoutId(name, defaultId);
        }
        return defaultId;
    }

    public static int getStyleId(String name, int defaultId) {
        final IDesktopController module = Admin.getInstance().get(DesktopController.NAME);
        if (module != null) {
            return module.getStyleId(name, defaultId);
        }
        return defaultId;
    }

    public static int getMenuId(String name, int defaultId) {
        final IDesktopController module = Admin.getInstance().get(DesktopController.NAME);
        if (module != null) {
            return module.getMenuId(name, defaultId);
        }
        return defaultId;
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
            return (T) provider.getDb(klass, databaseName);
        }
        return null;
    }

    /**
     * Получить текст ошибки по его коду
     *
     * @param errorCode код ошибки
     */
    public static String getErrorText(final int errorCode) {
        final Context context = ApplicationController.getInstance().getApplicationContext();
        if (context == null) {
            return "Error get context";
        }

        switch (errorCode) {
            case ErrorController.ERROR_LOST_APPLICATION_CONTEXT:
                return context.getString(R.string.error_db_app_not_loaded);

            case ErrorController.ERROR_GET_DATA:
                return context.getString(R.string.error_get_data);

            case ErrorController.ERROR_DB:
                return context.getString(R.string.error_db);

            case ErrorController.ERROR_NOT_FOUND_ACTIVITY:
                return context.getString(R.string.error_not_found_activity);

            case ErrorController.ERROR_ACTIVITY_NOT_VALID:
                return context.getString(R.string.error_activity_not_valid);

            case ErrorController.ERROR_GEOCODER_NOT_FOUND:
                return context.getString(R.string.error_geocoder_not_found);

            default:
                return context.getString(R.string.error_application);

        }

    }

    /**
     * Проверить - жива ли activity
     *
     * @return true - activity находится в рабочем состоянии
     */
    public static boolean isLivingActivity(final String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            final INavigationController controller = Admin.getInstance().get(NavigationController.NAME);
            if (controller != null) {
                final AbstractActivity activity = controller.getActivity(name);
                if (activity != null) {
                    if (activity.validate()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Проверить - жив ли presenter
     *
     * @return true - preseneter находится в рабочем состоянии
     */
    public static boolean isLivingPresenter(final String name) {
        if (!StringUtils.isNullOrEmpty(name)) {
            final IPresenterController controller = Admin.getInstance().get(PresenterController.NAME);
            if (controller != null) {
                final IPresenter presenter = controller.getPresenter(name);
                if (presenter != null) {
                    if (presenter.validate()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Получить модуль преобразования данных
     *
     * @return модуль преобразования данных
     */
    public static ITransformDataModule getTransformDataModule() {
        return Admin.getInstance().get(TransformDataModule.NAME);
    }

    /**
     * Получить модуль валидации
     *
     * @return модуль валидации
     */
    public static IValidateController getValidateController() {
        return Admin.getInstance().get(ValidateController.NAME);
    }

    /**
     * Получить модуль уведомлений
     *
     * @return модуль уведомлений
     */
    public static INotificationModule getNotificationModule() {
        return Admin.getInstance().get(NotificationModule.NAME);
    }

    /**
     * Получить модуль Application Preferences
     *
     * @return модуль Application Preferences
     */
    public static IPreferencesModule getPreferences() {
        return Admin.getInstance().get(PreferencesModule.NAME);
    }

    public static void viewLog() {
        final Context context = getActivity();
        if (context == null) {
            return;
        }

        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            try {
                final File log = new File(ErrorController.getInstance().getPath());
                final String file = log.getName();
                final String externalPath = ApplicationController.getInstance().getExternalDataPath() + file;
                final File external = new File(externalPath);
                if (!BuildConfig.DEBUG) {
                    if (external.exists()) {
                        external.delete();
                    }
                    Files.copy(log, external);
                }
                final Intent intent = IntentUtils.getViewDocumentIntent(getContext(), external);
                startChooser(intent, context.getString(R.string.log_view_title));
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            }
        } else {
            grantPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getString(R.string.permission_write_external_storage));
        }
    }

    public static void startChooser(final Intent intent, final String title) {
        if (intent == null || StringUtils.isNullOrEmpty(title)) {
            return;
        }

        final Context context = getActivity();
        if (context == null) {
            return;
        }

        try {
            context.startActivity(Intent.createChooser(intent, title));
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
    }

    public static void showShortcutBadger(final int count) {
        if (count >= 0) {
            final Context context = getContext();
            if (context != null) {
                ShortcutBadger.applyCount(context, count);
            }
        }
    }

    public static void hideShortcutBadger() {
        final Context context = getContext();
        if (context != null) {
            ShortcutBadger.removeCount(context);
        }
    }

    public static String getString(@StringRes final int resId) {
        final Context context = getContext();
        if (context != null) {
            return context.getString(resId);
        }
        return null;
    }

    public static String getString(@StringRes int resId, Object... formatArgs) {
        final Context context = getContext();
        if (context != null) {
            return context.getString(resId, formatArgs);
        }
        return null;
    }

    private AdminUtils() {
    }

}

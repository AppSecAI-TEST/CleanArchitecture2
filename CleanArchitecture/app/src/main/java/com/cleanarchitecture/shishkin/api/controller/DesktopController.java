package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;
import android.os.Bundle;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.ShowFragmentEvent;
import com.cleanarchitecture.shishkin.api.event.ui.DialogResultEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowListDialogEvent;
import com.cleanarchitecture.shishkin.api.mail.RecreateMail;
import com.cleanarchitecture.shishkin.api.ui.dialog.MaterialDialogExt;
import com.cleanarchitecture.shishkin.api.ui.fragment.SettingsDesktopOrderFragment;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DesktopController implements IDesktopController, IModuleSubscriber {
    public static final String NAME = DesktopController.class.getName();
    public static final String SUBSCRIBER_TYPE = IDesktopSubscriber.class.getName();

    private String mDesktop = ""; // default desktop
    private Map<String, String> mDesktops = Collections.synchronizedMap(new ConcurrentHashMap<String, String>());

    public DesktopController() {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            mDesktop = AppPreferences.getDesktop(context);

            String[] keys = context.getResources().getStringArray(R.array.desktop_key);
            String[] values = context.getResources().getStringArray(R.array.desktop_value);
            if (keys.length == values.length) {
                for (int i = 0; i < keys.length; i++) {
                    mDesktops.put(keys[i], values[i]);
                }
            }
        }
    }

    @Override
    public int getLayoutId(final String name, final int defaultId) {
        return getResourceId(name, "layout", defaultId);
    }

    @Override
    public int getStyleId(final String name, final int defaultId) {
        return getResourceId(name, "style", defaultId);
    }

    @Override
    public int getMenuId(final String name, final int defaultId) {
        return getResourceId(name, "menu", defaultId);
    }

    @Override
    public int getResourceId(final String name, final String type, final int defaultId) {
        if (!StringUtils.isNullOrEmpty(name) && !StringUtils.isNullOrEmpty(type)) {
            final Context context = AdminUtils.getContext();
            if (context != null) {
                String resource = name;
                if (!StringUtils.isNullOrEmpty(mDesktop)) {
                    resource += "_" + mDesktop;
                }
                int resId = ApplicationUtils.getResourceId(context, type, resource);
                if (resId != 0) {
                    return resId;
                }
            }
        }
        return defaultId;
    }

    @Override
    public synchronized void setDesktop(String desktop) {
        mDesktop = desktop;
    }

    @Override
    public synchronized void getDesktop() {
        final ArrayList<String> list = new ArrayList<>();
        for (String key : mDesktops.keySet()) {
            list.add(key);
        }
        if (list.size() > 1) {
            AdminUtils.postEvent(new ShowListDialogEvent(R.id.dialog_select_desktop, R.string.dialog_desktop_title, null, list, MaterialDialogExt.NO_BUTTON, MaterialDialogExt.NO_BUTTON, true));
        }
    }

    @Override
    public String getDesktopOrder(String name, String defaultOrder) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            return AppPreferences.getDesktopOrder(context, name, defaultOrder);
        }
        return defaultOrder;
    }

    @Override
    public void setDesktopOrder(String name, String defaultOrder) {
        AdminUtils.postEvent(new ShowFragmentEvent(SettingsDesktopOrderFragment.newInstance(name, defaultOrder)));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return SUBSCRIBER_TYPE;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void onUnRegister() {
    }

    @Override
    public List<String> hasSubscriberType() {
        final ArrayList<String> list = new ArrayList<>();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDialogResultEvent(DialogResultEvent event) {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            final Bundle bundle = event.getResult();
            if (bundle != null && bundle.getInt("id", -1) == R.id.dialog_select_desktop) {
                final ArrayList<String> list = bundle.getStringArrayList("list");
                if (list != null && list.size() > 0) {
                    if (mDesktops.containsKey(list.get(0))) {
                        mDesktop = mDesktops.get(list.get(0));
                        AppPreferences.setDesktop(context, mDesktop);
                        final INavigationController controller = Admin.getInstance().get(NavigationController.NAME);
                        if (controller != null) {
                            final Map<String, WeakReference<INavigationSubscriber>> map = controller.getSubscribers();
                            for (WeakReference<INavigationSubscriber> ref : map.values()) {
                                if (ref != null && ref.get() != null) {
                                    AdminUtils.addMail(new RecreateMail(ref.get().getName()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

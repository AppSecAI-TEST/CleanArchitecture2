package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;
import android.os.Bundle;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.ui.DialogResultEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowListDialogEvent;
import com.cleanarchitecture.shishkin.api.mail.RecreateMail;
import com.cleanarchitecture.shishkin.api.ui.dialog.MaterialDialogExt;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesktopController implements IDesktopController, IModuleSubscriber {
    public static final String NAME = DesktopController.class.getName();

    private String mDesktop = ""; // default desktop
    private Map<String, String> mDesktops = Collections.synchronizedMap(new HashMap<String, String>());

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
        if (!StringUtils.isNullOrEmpty(name)) {
            final Context context = AdminUtils.getContext();
            if (context != null) {
                String desktop = name;
                if (!StringUtils.isNullOrEmpty(mDesktop)) {
                    desktop += "_" + mDesktop;
                }
                int resId = ApplicationUtils.getResourceId(context, "layout", desktop);
                if (resId != -1) {
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
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return null;
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
                        final ILifecycleController controller = Admin.getInstance().get(LifecycleController.NAME);
                        if (controller != null) {
                            final Map<String, WeakReference<ILifecycleSubscriber>> map = controller.getSubscribers();
                            for (WeakReference<ILifecycleSubscriber> ref : map.values()) {
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

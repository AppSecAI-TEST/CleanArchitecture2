package com.cleanarchitecture.shishkin.api.presenter;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.ui.adapter.SettingsDesktopOrderRecyclerViewAdapter;
import com.cleanarchitecture.shishkin.api.ui.fragment.SettingsDesktopOrderFragment;
import com.cleanarchitecture.shishkin.api.ui.item.SettingsDesktopOrderItem;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.MoveTouchHelper;
import com.cleanarchitecture.shishkin.common.utils.SerializableUtil;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SettingsDesktopOrderPresenter extends AbstractPresenter<Void> {
    public static final String NAME = SettingsDesktopOrderPresenter.class.getName();
    public static final String LOG_TAG = "SettingsDesktopOrderPresenter:";

    private SettingsDesktopOrderRecyclerViewAdapter mSettingsAdapter;
    private RecyclerView mRecyclerView;
    private Bundle mArgs;

    public void bindView(final View root, final Bundle args) {

        if (root == null || args == null) {
            return;
        }

        mArgs = args;

        mRecyclerView = ViewUtils.findView(root, R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSettingsAdapter = new SettingsDesktopOrderRecyclerViewAdapter(root.getContext(), mArgs.getString(SettingsDesktopOrderFragment.ORDER_NAME));
        mSettingsAdapter.addAll(getItems());
        mRecyclerView.setAdapter(mSettingsAdapter);

        final ItemTouchHelper.Callback callback = new MoveTouchHelper(mSettingsAdapter);
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);
    }

    private List<SettingsDesktopOrderItem> getItems() {
        final Type type = new com.google.gson.reflect.TypeToken<List<SettingsDesktopOrderItem>>() {
        }.getType();
        final List<SettingsDesktopOrderItem> list = new ArrayList<>();

        final String order = AdminUtils.getPreferences().getDesktopOrder(mArgs.getString(SettingsDesktopOrderFragment.ORDER_NAME), mArgs.getString(SettingsDesktopOrderFragment.ORDER));
        try {
            list.addAll(SerializableUtil.fromJson(order, type));
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }

        if (list.isEmpty()) {
            try {
                list.addAll(SerializableUtil.fromJson(mArgs.getString(SettingsDesktopOrderFragment.ORDER), type));
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            }
        }
        return list;
    }

    @Override
    public void onDestroyState() {
        super.onDestroyState();

        mRecyclerView.setAdapter(null);
    }

    public void save() {
        mSettingsAdapter.save();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isRegister() {
        return false;
    }
}

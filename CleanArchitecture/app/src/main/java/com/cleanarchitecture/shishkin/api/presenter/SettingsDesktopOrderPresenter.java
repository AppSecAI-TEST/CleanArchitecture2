package com.cleanarchitecture.shishkin.api.presenter;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.AppPreferences;
import com.cleanarchitecture.shishkin.api.ui.adapter.SettingsDesktopOrderRecyclerViewAdapter;
import com.cleanarchitecture.shishkin.api.ui.fragment.SettingsDesktopOrderFragment;
import com.cleanarchitecture.shishkin.api.ui.item.SettingsDesktopOrderItem;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.MoveTouchHelper;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SettingsDesktopOrderPresenter extends AbstractPresenter<Void> {
    public static final String NAME = SettingsDesktopOrderPresenter.class.getName();

    private SettingsDesktopOrderRecyclerViewAdapter mSettingsAdapter;
    private Bundle mArgs;

    public void bindView(final View root, final Bundle args) {

        if (root == null || args == null) {
            return;
        }

        mArgs = args;

        final RecyclerView recyclerView = ViewUtils.findView(root, R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mSettingsAdapter = new SettingsDesktopOrderRecyclerViewAdapter(root.getContext(), mArgs.getString(SettingsDesktopOrderFragment.ORDER_NAME));
        mSettingsAdapter.addAll(getItems());
        recyclerView.setAdapter(mSettingsAdapter);

        final ItemTouchHelper.Callback callback = new MoveTouchHelper(mSettingsAdapter);
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }

    private List<SettingsDesktopOrderItem> getItems() {
        final List<SettingsDesktopOrderItem> list = new ArrayList<>();

        final String order = AppPreferences.getDesktopOrder(AdminUtils.getContext(), mArgs.getString(SettingsDesktopOrderFragment.ORDER_NAME), mArgs.getString(SettingsDesktopOrderFragment.ORDER));
        int cnt = StringUtils.numToken(order, ";");
        final Gson gson = new Gson();
        for (int i = 1; i <= cnt; i++) {
            SettingsDesktopOrderItem item = gson.fromJson(StringUtils.token(order, ";", i), SettingsDesktopOrderItem.class);
            list.add(item);
        }
        return list;
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

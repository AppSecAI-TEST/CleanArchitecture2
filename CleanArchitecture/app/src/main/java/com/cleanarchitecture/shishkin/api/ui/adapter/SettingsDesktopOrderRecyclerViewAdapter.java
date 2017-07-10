package com.cleanarchitecture.shishkin.api.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.AppPreferencesModule;
import com.cleanarchitecture.shishkin.api.ui.item.SettingsDesktopOrderItem;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.AbstractRecyclerViewAdapter;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.AbstractViewHolder;
import com.cleanarchitecture.shishkin.common.utils.SerializableUtil;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

import java.util.LinkedList;
import java.util.List;

public class SettingsDesktopOrderRecyclerViewAdapter extends AbstractRecyclerViewAdapter<SettingsDesktopOrderItem, SettingsDesktopOrderRecyclerViewAdapter.ViewHolder> {

    private String mOrderName;

    public SettingsDesktopOrderRecyclerViewAdapter(@NonNull Context context, final String orderName) {
        super(context);

        mOrderName = orderName;

        setHasStableIds(true);
    }

    @NonNull
    @Override
    public SettingsDesktopOrderRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        final View view = inflater.inflate(AdminUtils.getLayoutId("setting_item_switch", R.layout.setting_item_switch), parent, false);
        return new SettingsDesktopOrderRecyclerViewAdapter.ViewHolder(view);
    }

    public void save() {
        final Context context = AdminUtils.getContext();
        if (context != null) {
            final List<SettingsDesktopOrderItem> items = new LinkedList<>();
            for (int i = 0; i < getItemCount(); i++) {
                items.add(getItem(i));
            }
            AppPreferencesModule.getInstance().setDesktopOrder(mOrderName, (String) SerializableUtil.toJson(items));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsDesktopOrderRecyclerViewAdapter.ViewHolder holder, SettingsDesktopOrderItem item, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends AbstractViewHolder implements CompoundButton.OnCheckedChangeListener {

        private TextView mTextView;
        private SwitchCompat mSwitchView;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);

            mTextView = ViewUtils.findView(itemView, R.id.item_title);
            mSwitchView = ViewUtils.findView(itemView, R.id.item_switch);
        }

        void bind(@NonNull final SettingsDesktopOrderItem item) {
            mTextView.setText(item.getId());

            mSwitchView.setChecked(item.isEnabled());
            mSwitchView.setTag(item);
            mSwitchView.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final SettingsDesktopOrderItem item = (SettingsDesktopOrderItem) buttonView.getTag();
            if (item != null) {
                item.setEnabled(isChecked);
            }
        }

    }

}

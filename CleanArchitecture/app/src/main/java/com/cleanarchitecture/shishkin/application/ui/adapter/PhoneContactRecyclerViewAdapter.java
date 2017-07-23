package com.cleanarchitecture.shishkin.application.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.event.livedata.LiveDataHasDataEvent;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.AbstractRecyclerViewAdapter;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.AbstractViewHolder;
import com.cleanarchitecture.shishkin.application.Constant;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.phonecontactpresenter.OnPhoneContactPresenterItemClick;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;
import com.pkmmte.view.CircularImageView;

public class PhoneContactRecyclerViewAdapter extends AbstractRecyclerViewAdapter<PhoneContactItem, PhoneContactRecyclerViewAdapter.ViewHolder> {

    public PhoneContactRecyclerViewAdapter(@NonNull Context context) {
        super(context);

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return StringUtils.toLong(getItem(position).getId());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int viewType) {
        final View view = inflater.inflate(AdminUtils.getLayoutId("list_contact_item", R.layout.list_contact_item), parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, PhoneContactItem item, int position) {
        holder.bind(getItemCount(), getItem(position));
    }

    static class ViewHolder extends AbstractViewHolder {

        private TextView mTextView;
        private CircularImageView mImageView;
        private View mLayout;
        private View mDivider;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);

            mTextView = findView(R.id.text);
            mImageView = findView(R.id.image);
            mLayout = findView(R.id.ll);
            mLayout.setOnClickListener(this::onClick);
            mDivider = findView(R.id.divider);
        }

        void bind(final int cnt, @NonNull final PhoneContactItem item) {
            mTextView.setText(item.getName());
            mLayout.setTag(item);
            if (!StringUtils.isNullOrEmpty(item.getPhoto())) {
                mImageView.setImageURI(Uri.parse(item.getPhoto()));
            } else {
                mImageView.setImageDrawable(ViewUtils.getDrawable(mImageView.getContext(), R.mipmap.ic_account));
            }
            final int position = getAdapterPosition();
            mDivider.setVisibility(position == cnt - 1 ? View.VISIBLE : View.GONE);

            if (position == cnt - 40) {
                AdminUtils.postEvent(new LiveDataHasDataEvent().setId(Constant.REPOSITORY_REQUEST_CURSOR_GET_CONTACTS_EVENT));
            }

        }

        private void onClick(View v) {
            AdminUtils.postEvent(new OnPhoneContactPresenterItemClick((PhoneContactItem) v.getTag()));
        }
    }

}

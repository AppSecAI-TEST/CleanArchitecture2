package com.cleanarchitecture.shishkin.application.presenter;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.EditText;

import com.annimon.stream.Stream;
import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.application.app.ApplicationController;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryResponseGetContactsEvent;
import com.cleanarchitecture.shishkin.application.event.searchpresenter.OnSearchPresenterItemClick;
import com.cleanarchitecture.shishkin.application.ui.adapter.ContactRecyclerViewAdapter;
import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.controller.LifecycleController;
import com.cleanarchitecture.shishkin.base.event.OnPermisionGrantedEvent;
import com.cleanarchitecture.shishkin.base.event.ui.DialogResultEvent;
import com.cleanarchitecture.shishkin.base.event.ui.HideHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowHorizontalProgressBarEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowListDialogEvent;
import com.cleanarchitecture.shishkin.base.event.ui.ShowToastEvent;
import com.cleanarchitecture.shishkin.base.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.base.presenter.AbstractContentProviderPresenter;
import com.cleanarchitecture.shishkin.base.repository.Repository;
import com.cleanarchitecture.shishkin.base.ui.dialog.MaterialDialogExt;
import com.cleanarchitecture.shishkin.base.ui.fragment.AbstractContentFragment;
import com.cleanarchitecture.shishkin.base.ui.recyclerview.OnScrollListener;
import com.cleanarchitecture.shishkin.base.ui.recyclerview.SwipeTouchHelper;
import com.cleanarchitecture.shishkin.base.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.base.utils.PhoneUtils;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;
import com.cleanarchitecture.shishkin.base.utils.ViewUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


@SuppressWarnings("unused")
public class SearchPresenter extends AbstractContentProviderPresenter<List<PhoneContactItem>> implements Consumer<TextViewAfterTextChangeEvent> {
    public static final String NAME = "SearchPresenter";

    private WeakReference<EditText> mSearchView;
    private WeakReference<FastScrollRecyclerView> mRecyclerView;
    private ContactRecyclerViewAdapter mContactAdapter;
    private String mCurrentFilter = null;
    private Disposable mDisposableSearchView;
    private LinearLayoutManager mLinearLayoutManager;

    public SearchPresenter(@NonNull final Uri uri) {
        super(uri);
    }

    public void bindView(@NonNull final View root, final AbstractContentFragment fragment) {

        EventController.getInstance().register(this);

        final EditText searchView = ViewUtils.findView(root, R.id.search);
        if (searchView != null) {
            mDisposableSearchView = RxTextView.afterTextChangeEvents(searchView)
                    .observeOn(AndroidSchedulers.mainThread())
                    .debounce(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribe(this);

            mSearchView = new WeakReference<>(searchView);
            searchView.setText(mCurrentFilter);
        }

        final FastScrollRecyclerView recyclerView = ViewUtils.findView(root, R.id.list);
        if (recyclerView != null) {
            mLinearLayoutManager = new LinearLayoutManager(LifecycleController.getInstance().getActivity());
            recyclerView.setLayoutManager(mLinearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            mContactAdapter = new ContactRecyclerViewAdapter(root.getContext());
            recyclerView.setAdapter(mContactAdapter);

            final ItemTouchHelper.Callback callback = new SwipeTouchHelper(mContactAdapter);
            final ItemTouchHelper helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(recyclerView);

            final SwipeRefreshLayout swipeRefreshLayout = ViewUtils.findView(root, R.id.swipeRefreshLayout);
            if (fragment != null && fragment.getContentFragmentPresenter() != null) {
                fragment.getContentFragmentPresenter().setSwipeRefreshLayout(swipeRefreshLayout);
            }
            recyclerView.addOnScrollListener(new OnScrollListener(recyclerView, swipeRefreshLayout));

            mRecyclerView = new WeakReference<>(recyclerView);
        }
    }

    @Override
    public void onDestroyLifecycle() {
        mSearchView = null;
        mRecyclerView = null;
        if (!mDisposableSearchView.isDisposed()) {
            mDisposableSearchView.dispose();
        }

        EventController.getInstance().unregister(this);

        super.onDestroyLifecycle();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isRegister() {
        return true;
    }

    @Override
    public boolean validate() {
        return (super.validate()
                && mSearchView != null && mSearchView.get() != null
                && mRecyclerView != null && mRecyclerView.get() != null
        );
    }

    @Override
    public void onContentChanged() {
        EventController.getInstance().post(new ShowHorizontalProgressBarEvent());
        EventController.getInstance().post(new RepositoryRequestGetContactsEvent(Repository.USE_SAVE_CACHE));
    }

    private List<PhoneContactItem> filter(final String pattern) {
        return Stream.of(getModel()).filter(item -> StringUtils.containsIgnoreCase(item.getName(), pattern)).toList();
    }

    public void refreshData() {
        onContentChanged();
    }

    @Override
    public void accept(@io.reactivex.annotations.NonNull TextViewAfterTextChangeEvent event) {
        onTextViewAfterTextChangeEvent(event);
    }

    private void onTextViewAfterTextChangeEvent(TextViewAfterTextChangeEvent event) {
        if (validate()) {
            mCurrentFilter = event.view().getText().toString();
            if (getModel() != null && !getModel().isEmpty()) {
                updateView();
            } else {
                EventController.getInstance().post(new ShowHorizontalProgressBarEvent());
                EventController.getInstance().post(new RepositoryRequestGetContactsEvent(Repository.USE_ONLY_CACHE));
            }
        }
    }

    @Override
    public void updateView() {
        ApplicationUtils.runOnUiThread(() -> {
            if (getModel() != null) {
                if (!StringUtils.isNullOrEmpty(mCurrentFilter)) {
                    final List<PhoneContactItem> list = filter(mCurrentFilter);
                    mContactAdapter.setItems(list);
                } else {
                    mContactAdapter.setItems(getModel());
                }

                if (!mContactAdapter.isEmpty()) {
                    EventController.getInstance().post(new HideKeyboardEvent());
                } else {
                    EventController.getInstance().post(new ShowToastEvent(ApplicationController.getInstance().getString(R.string.no_data)));
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onResponseGetContactsEvent(RepositoryResponseGetContactsEvent event) {
        EventController.getInstance().post(new HideHorizontalProgressBarEvent());
        List<PhoneContactItem> list = event.getContacts();
        if (list == null) {
            list = new ArrayList<>();
        }
        setModel(list);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onSearchPresenterItemClick(OnSearchPresenterItemClick event) {
        if (event.getContactItem() != null) {
            final PhoneContactItem item = event.getContactItem();
            final ArrayList<String> list = new ArrayList<>();
            for (int i = 1; i <= StringUtils.numToken(item.getPhones(), ";"); i++) {
                String phone = StringUtils.token(item.getPhones(), ";", i);
                String phone1 = null;
                if (ApplicationUtils.hasLollipop()) {
                    phone1 = PhoneNumberUtils.formatNumber(phone, Locale.getDefault().getCountry());
                }
                if (StringUtils.isNullOrEmpty(phone1)) {
                    list.add(phone);
                } else {
                    list.add(phone1);
                }
            }
            EventController.getInstance().post(new ShowListDialogEvent(R.id.dialog_call_phone, R.string.phone_call, null, list, -1, R.string.exit, true));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onDialogResultEvent(DialogResultEvent event) {
        if (ApplicationUtils.checkPermission(Manifest.permission.CALL_PHONE)) {
            final Bundle bundle = event.getResult();
            if (bundle.getInt("id") == R.id.dialog_call_phone) {
                if (bundle.getString(MaterialDialogExt.BUTTON).equals(MaterialDialogExt.POSITIVE)) {
                    final ArrayList<String> list = bundle.getStringArrayList("list");
                    if (list != null && list.size() == 1) {
                        PhoneUtils.call(LifecycleController.getInstance().getActivity(), list.get(0));
                    }
                }
            }
        } else {
            postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.CALL_PHONE));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onPermisionGrantedEvent(OnPermisionGrantedEvent event) {
        if (event.getPermission().equals(Manifest.permission.READ_CONTACTS)) {
            refreshData();
        }
    }
}

package com.cleanarchitecture.shishkin.application.presenter;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.EditText;

import com.annimon.stream.Stream;
import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.event.OnPermisionGrantedEvent;
import com.cleanarchitecture.shishkin.api.event.ui.DialogResultEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowListDialogEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowToastEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.api.presenter.AbstractPresenter;
import com.cleanarchitecture.shishkin.api.repository.IDbProvider;
import com.cleanarchitecture.shishkin.api.repository.IObserver;
import com.cleanarchitecture.shishkin.api.repository.Repository;
import com.cleanarchitecture.shishkin.api.ui.dialog.MaterialDialogExt;
import com.cleanarchitecture.shishkin.api.ui.fragment.AbstractContentFragment;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.OnScrollListener;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.SwipeTouchHelper;
import com.cleanarchitecture.shishkin.application.app.Constant;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.data.viewmodel.PhoneContactViewModel;
import com.cleanarchitecture.shishkin.application.event.phonecontactpresenter.OnPhoneContactPresenterItemClick;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.ui.adapter.PhoneContactRecyclerViewAdapter;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.PhoneUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;
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
public class PhoneContactPresenter extends AbstractPresenter<List<PhoneContactItem>>
        implements Consumer<TextViewAfterTextChangeEvent>, IObserver<List<PhoneContactItem>> {
    public static final String NAME = PhoneContactPresenter.class.getName();

    private WeakReference<EditText> mSearchView;
    private WeakReference<FastScrollRecyclerView> mRecyclerView;
    private PhoneContactRecyclerViewAdapter mContactAdapter;
    private String mCurrentFilter = null;
    private Disposable mDisposableSearchView;
    private IDbProvider mDbProvider = AdminUtils.getDbProvider();

    public PhoneContactPresenter() {
        super();
    }

    public void bindView(@NonNull final View root, final AbstractContentFragment fragment) {

        final FastScrollRecyclerView recyclerView = ViewUtils.findView(root, R.id.list);
        if (recyclerView != null) {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AdminUtils.getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            mContactAdapter = new PhoneContactRecyclerViewAdapter(root.getContext());
            recyclerView.setAdapter(mContactAdapter);

            final ItemTouchHelper.Callback callback = new SwipeTouchHelper(mContactAdapter);
            final ItemTouchHelper helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(recyclerView);

            recyclerView.addOnScrollListener(new OnScrollListener(recyclerView, fragment.getSwipeRefreshLayout()));

            mRecyclerView = new WeakReference<>(recyclerView);
        }

        final EditText searchView = ViewUtils.findView(root, R.id.search);
        if (searchView != null) {
            mDisposableSearchView = RxTextView.afterTextChangeEvents(searchView)
                    .observeOn(AndroidSchedulers.mainThread())
                    .debounce(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribe(this);

            mSearchView = new WeakReference<>(searchView);
            searchView.setText(mCurrentFilter);
        }

        if (mDbProvider != null) {
            mDbProvider.observe(fragment.getLifecycleActivity(), PhoneContactViewModel.NAME, PhoneContactViewModel.class, this);
        }
    }

    @Override
    public void onDestroyLifecycle() {
        super.onDestroyLifecycle();

        if (mDbProvider != null) {
            mDbProvider.removeObserver(PhoneContactViewModel.NAME, this);
        }
        mSearchView = null;
        mRecyclerView.get().clearOnScrollListeners();
        mRecyclerView.get().setAdapter(null);
        mRecyclerView = null;
        if (!mDisposableSearchView.isDisposed()) {
            mDisposableSearchView.dispose();
        }

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<String> hasSubscriberType() {
        final List<String> list = super.hasSubscriberType();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        return list;
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

    private List<PhoneContactItem> filter(final String pattern) {
        return Stream.of(getModel()).filter(item -> StringUtils.containsIgnoreCase(item.getName(), pattern)).toList();
    }

    public void refreshData() {
        AdminUtils.postEvent(new RepositoryRequestGetContactsEvent()
                .setCacheType(Repository.USE_SAVE_CACHE)
                .setId(Constant.REPOSITORY_GET_CONTACTS)
        );
    }

    @Override
    public void accept(@io.reactivex.annotations.NonNull TextViewAfterTextChangeEvent event) {
        if (validate()) {
            mCurrentFilter = event.view().getText().toString();
            updateView();
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
                    AdminUtils.postEvent(new HideKeyboardEvent());
                } else {
                    AdminUtils.postEvent(new ShowToastEvent(AdminUtils.getContext().getString(R.string.no_data)));
                }
            }
        });
    }

    @Override
    public void onChanged(@Nullable List<PhoneContactItem> list) {
        setModel(list);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onSearchPresenterItemClick(OnPhoneContactPresenterItemClick event) {
        if (AdminUtils.checkPermission(Manifest.permission.CALL_PHONE)) {
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
                AdminUtils.postEvent(new ShowListDialogEvent(R.id.dialog_call_phone, R.string.phone_call, null, list, -1, R.string.exit, true));
            }
        } else {
            AdminUtils.postEvent(new UseCaseRequestPermissionEvent(Manifest.permission.CALL_PHONE));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onDialogResultEvent(DialogResultEvent event) {
        if (AdminUtils.checkPermission(Manifest.permission.CALL_PHONE)) {
            final Bundle bundle = event.getResult();
            if (bundle != null && bundle.getInt("id", -1) == R.id.dialog_call_phone) {
                if (MaterialDialogExt.POSITIVE.equals(bundle.getString(MaterialDialogExt.BUTTON))) {
                    final ArrayList<String> list = bundle.getStringArrayList("list");
                    if (list != null && list.size() == 1) {
                        PhoneUtils.call(AdminUtils.getActivity(), list.get(0));
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onPermisionGrantedEvent(OnPermisionGrantedEvent event) {
        if (event.getPermission().equals(Manifest.permission.READ_CONTACTS)) {
            refreshData();
        }
    }

}

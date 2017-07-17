package com.cleanarchitecture.shishkin.application.presenter;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.ITransformDataModule;
import com.cleanarchitecture.shishkin.api.controller.IValidateSubscriber;
import com.cleanarchitecture.shishkin.api.controller.ValidateController;
import com.cleanarchitecture.shishkin.api.debounce.Debounce;
import com.cleanarchitecture.shishkin.api.event.OnPermisionGrantedEvent;
import com.cleanarchitecture.shishkin.api.event.ui.DialogResultEvent;
import com.cleanarchitecture.shishkin.api.event.ui.EditTextAfterTextChangedEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideCircleProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowCircleProgressBarEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowKeyboardEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowListDialogEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowToastEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowTooltipEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.api.observer.EditTextDebouncedObserver;
import com.cleanarchitecture.shishkin.api.presenter.AbstractPresenter;
import com.cleanarchitecture.shishkin.api.repository.IDbProvider;
import com.cleanarchitecture.shishkin.api.repository.IObserver;
import com.cleanarchitecture.shishkin.api.storage.CacheUtils;
import com.cleanarchitecture.shishkin.api.ui.dialog.MaterialDialogExt;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.OnScrollListener;
import com.cleanarchitecture.shishkin.api.validate.IValidator;
import com.cleanarchitecture.shishkin.application.Constant;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.data.viewmodel.PhoneContactViewModel;
import com.cleanarchitecture.shishkin.application.event.phonecontactpresenter.OnPhoneContactPresenterItemClick;
import com.cleanarchitecture.shishkin.application.event.repository.RepositoryRequestGetContactsEvent;
import com.cleanarchitecture.shishkin.application.ui.adapter.PhoneContactRecyclerViewAdapter;
import com.cleanarchitecture.shishkin.application.validate.PhoneContactItemValidator;
import com.cleanarchitecture.shishkin.common.utils.ApplicationUtils;
import com.cleanarchitecture.shishkin.common.utils.PhoneUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class PhoneContactPresenter extends AbstractPresenter<List<PhoneContactItem>>
        implements IObserver<List<PhoneContactItem>>, IValidateSubscriber {
    public static final String NAME = PhoneContactPresenter.class.getName();
    private static final String CURRENT_FILTER = "CURRENT_FILTER";

    private WeakReference<EditText> mSearchView;
    private WeakReference<RecyclerView> mRecyclerView;
    private PhoneContactRecyclerViewAdapter mContactAdapter;
    private String mCurrentFilter = null;
    private IDbProvider mDbProvider = AdminUtils.getDbProvider();
    private EditTextDebouncedObserver mEditTextDebouncedObserver;
    private Debounce mDebounce = new Debounce(TimeUnit.SECONDS.toMillis(8)) {
        @Override
        public void run() {
            AdminUtils.postEvent(new HideKeyboardEvent());
        }
    };

    public PhoneContactPresenter() {
        super();
    }

    public void bindView(@NonNull final View root) {

        final Bundle bundle = AdminUtils.getStateData(NAME);

        final RecyclerView recyclerView = ViewUtils.findView(root, R.id.list);
        if (recyclerView != null) {
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AdminUtils.getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            mContactAdapter = new PhoneContactRecyclerViewAdapter(root.getContext());
            recyclerView.setAdapter(mContactAdapter);

            final SwipeRefreshLayout swipeRefreshLayout = ViewUtils.findView(root, R.id.swipeRefreshLayout);
            recyclerView.addOnScrollListener(new OnScrollListener(recyclerView, swipeRefreshLayout));

            mRecyclerView = new WeakReference<>(recyclerView);
        }

        final EditText searchView = ViewUtils.findView(root, R.id.search);
        if (searchView != null) {
            if (bundle != null) {
                mCurrentFilter = bundle.getString(CURRENT_FILTER);
                mContactAdapter.clear();
            }

            mEditTextDebouncedObserver = new EditTextDebouncedObserver(searchView, 700, R.id
                    .edittext_phone_contact_presenter);
            mSearchView = new WeakReference<>(searchView);
            searchView.setText(mCurrentFilter);
            AdminUtils.postEvent(new HideKeyboardEvent());
        }

        if (mDbProvider != null) {
            mDbProvider.observe(AdminUtils.getActivity(), PhoneContactViewModel.NAME, PhoneContactViewModel.class, this);
        }

        for (int i = 0; i <= 11; i++) {
            if (i < 11) {
                final int ii = i;
                root.postDelayed(() -> AdminUtils.postEvent(new ShowCircleProgressBarEvent(ii * 10)), 100 * i);
            } else {
                root.postDelayed(() -> AdminUtils.postEvent(new HideCircleProgressBarEvent()), 100 * i);
                root.postDelayed(() -> {
                    if (validate()) {
                        mSearchView.get().requestFocus();
                        mSearchView.get().requestFocusFromTouch();
                        AdminUtils.postEvent(new ShowKeyboardEvent());
                    }
                }, 100 * i);
            }
        }
    }

    @Override
    public void onResumeLifecycle() {
        if (AdminUtils.getPreferences().getSettingShowTooltip()) {
            AdminUtils.postEvent(new ShowTooltipEvent(mSearchView.get(), R.string.tooltip_search, Gravity.BOTTOM));
        }
    }

    @Override
    public void onDestroyLifecycle() {
        super.onDestroyLifecycle();

        if (mDbProvider != null) {
            mDbProvider.removeObserver(PhoneContactViewModel.NAME, this);
        }

        mEditTextDebouncedObserver.finish();
        mSearchView = null;
        mRecyclerView.get().clearOnScrollListeners();
        mRecyclerView.get().setAdapter(null);
        mRecyclerView = null;
        mDebounce.finish();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public List<String> hasSubscriberType() {
        final List<String> list = super.hasSubscriberType();
        list.add(EventBusController.SUBSCRIBER_TYPE);
        list.add(ValidateController.SUBSCRIBER_TYPE);
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
        final ITransformDataModule module = AdminUtils.getTransformDataModule();
        if (module == null) {
            return getModel();
        }

        if (pattern.equalsIgnoreCase("*")) {
            return getModel();
        } else if (pattern.endsWith("*") && pattern.length() > 1) {
            final String templ = StringUtils.mid(pattern, 0, pattern.length() - 1);
            return module.filter(getModel(), item -> StringUtils.startsWith(item.getName(), templ));
        } else if (pattern.startsWith("*") && pattern.length() > 1) {
            final String templ = StringUtils.mid(pattern, 1);
            return module.filter(getModel(), item -> StringUtils.endsWith(item.getName(), templ));
        } else {
            return module.filter(getModel(), item -> StringUtils.containsIgnoreCase(item.getName(), pattern));
        }
    }

    public void refreshData() {
        AdminUtils.postEvent(new RepositoryRequestGetContactsEvent()
                .setExpired(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
                .setCacheType(CacheUtils.USE_SAVE_CACHE)
                .setId(Constant.REPOSITORY_GET_CONTACTS)
        );
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

                if (mContactAdapter.isEmpty()) {
                    AdminUtils.postEvent(new ShowToastEvent(AdminUtils.getContext().getString(R.string.no_data)));
                }
            }
        });
    }

    @Override
    public void onChanged(@Nullable List<PhoneContactItem> list) {
        setModel(list);
    }

    @Override
    public Bundle getStateData() {
        final Bundle bundle = new Bundle();
        bundle.putString(CURRENT_FILTER, mCurrentFilter);
        return bundle;
    }

    @Override
    public IValidator getValidator() {
        return new PhoneContactItemValidator();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onSearchPresenterItemClick(OnPhoneContactPresenterItemClick event) {
        if (AdminUtils.checkPermission(Manifest.permission.CALL_PHONE)) {
            if (event.getContactItem() != null) {
                final PhoneContactItem item = event.getContactItem();
                if (!AdminUtils.validate(this, item)) {
                    return;
                }

                final ArrayList<String> list = new ArrayList<>();
                for (int i = 1; i <= StringUtils.numToken(item.getPhones(), ";"); i++) {
                    String phone = StringUtils.token(item.getPhones(), ";", i);
                    if (AdminUtils.validate(this, phone)) {
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
                }
                if (!list.isEmpty()) {
                    AdminUtils.postEvent(new ShowListDialogEvent(R.id.dialog_call_phone, R.string.phone_call, null, list, -1, R.string.exit, true));
                }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onEditTextAfterTextChangedEvent(EditTextAfterTextChangedEvent event) {
        if (event.getId() == R.id.edittext_phone_contact_presenter) {
            mCurrentFilter = event.getText();
            updateView();
            mDebounce.onEvent();
        }
    }

}

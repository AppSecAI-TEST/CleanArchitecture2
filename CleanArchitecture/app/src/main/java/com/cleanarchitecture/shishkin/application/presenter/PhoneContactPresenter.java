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
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.controller.EventBusController;
import com.cleanarchitecture.shishkin.api.controller.ITransformDataModule;
import com.cleanarchitecture.shishkin.api.controller.IValidateController;
import com.cleanarchitecture.shishkin.api.controller.IValidateSubscriber;
import com.cleanarchitecture.shishkin.api.controller.ValidateController;
import com.cleanarchitecture.shishkin.api.data.ExtError;
import com.cleanarchitecture.shishkin.api.data.Result;
import com.cleanarchitecture.shishkin.api.debounce.Debounce;
import com.cleanarchitecture.shishkin.api.event.OnPermisionGrantedEvent;
import com.cleanarchitecture.shishkin.api.event.ui.DialogResultEvent;
import com.cleanarchitecture.shishkin.api.event.ui.EditTextAfterTextChangedEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowListDialogEvent;
import com.cleanarchitecture.shishkin.api.event.ui.ShowTooltipEvent;
import com.cleanarchitecture.shishkin.api.event.usecase.UseCaseRequestPermissionEvent;
import com.cleanarchitecture.shishkin.api.observer.EditTextDebouncedObserver;
import com.cleanarchitecture.shishkin.api.presenter.AbstractPresenter;
import com.cleanarchitecture.shishkin.api.repository.IDbProvider;
import com.cleanarchitecture.shishkin.api.repository.IObserver;
import com.cleanarchitecture.shishkin.api.ui.dialog.MaterialDialogExt;
import com.cleanarchitecture.shishkin.api.ui.recyclerview.OnScrollListener;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.application.data.livedata.PhoneContactLiveData;
import com.cleanarchitecture.shishkin.application.data.viewmodel.PhoneContactViewModel;
import com.cleanarchitecture.shishkin.application.event.phonecontactpresenter.OnPhoneContactPresenterItemClick;
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
    private IValidateController mValidateController = AdminUtils.getValidateController();

    public PhoneContactPresenter() {
        super();
    }

    public void bindView(@NonNull final View root) {

        final Bundle bundle = AdminUtils.getStateData(NAME);

        final RecyclerView recyclerView = ViewUtils.findView(root, R.id.list);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AdminUtils.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mContactAdapter = new PhoneContactRecyclerViewAdapter(root.getContext());
        recyclerView.setAdapter(mContactAdapter);

        final SwipeRefreshLayout swipeRefreshLayout = ViewUtils.findView(root, R.id.swipeRefreshLayout);
        recyclerView.addOnScrollListener(new OnScrollListener(recyclerView, swipeRefreshLayout));

        mRecyclerView = new WeakReference<>(recyclerView);

        final EditText searchView = ViewUtils.findView(root, R.id.search);
        if (bundle != null) {
            mCurrentFilter = bundle.getString(CURRENT_FILTER);
            mContactAdapter.clear();
        }

        mEditTextDebouncedObserver = new EditTextDebouncedObserver(searchView, 700, R.id
                .edittext_phone_contact_presenter);
        mSearchView = new WeakReference<>(searchView);
        searchView.setText(mCurrentFilter);
        AdminUtils.postEvent(new HideKeyboardEvent());

        mDbProvider.observe(PhoneContactViewModel.NAME, PhoneContactViewModel.class, this);
    }

    @Override
    public void onResumeState() {
        super.onResumeState();

        if (AdminUtils.getPreferences().getSettingShowTooltip()) {
            AdminUtils.postEvent(new ShowTooltipEvent(mSearchView.get(), R.string.tooltip_search, Gravity.BOTTOM));
        }
    }

    @Override
    public void onDestroyState() {
        super.onDestroyState();

        mDbProvider.removeObserver(PhoneContactViewModel.NAME, this);

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
    public List<String> getSubscription() {
        return StringUtils.arrayToList(
                super.getSubscription(),
                EventBusController.NAME,
                ValidateController.NAME
        );
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
            return module.filter(getModel(), item -> StringUtils.startsWith(item.getName(), templ)).toList();
        } else if (pattern.startsWith("*") && pattern.length() > 1) {
            final String templ = StringUtils.mid(pattern, 1);
            return module.filter(getModel(), item -> StringUtils.endsWith(item.getName(), templ)).toList();
        } else {
            return module.filter(getModel(), item -> StringUtils.containsIgnoreCase(item.getName(), pattern)).toList();
        }
    }

    public PhoneContactLiveData getLiveData() {
        final PhoneContactViewModel model = (PhoneContactViewModel) mDbProvider.getViewModel(PhoneContactViewModel.NAME);
        return (PhoneContactLiveData) model.getLiveData();
    }

    public void refreshData() {
        final PhoneContactViewModel model = (PhoneContactViewModel) mDbProvider.getViewModel(PhoneContactViewModel.NAME);
        ((PhoneContactLiveData) model.getLiveData()).getData();
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
    public List<String> hasValidatorType() {
        return StringUtils.arrayToList(PhoneContactItemValidator.NAME);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public synchronized void onPhoneContactPresenterItemClick(OnPhoneContactPresenterItemClick event) {
        final PhoneContactItem item = event.getContactItem();
        if (item == null || mValidateController == null) {
            return;
        }

        if (AdminUtils.checkPermission(Manifest.permission.CALL_PHONE)) {
            Result<Boolean> result = mValidateController.validate(PhoneContactItemValidator.NAME, item);
            if (!result.getResult()) {
                ErrorController.getInstance().onError(result.getError());
                return;
            }

            final ExtError err = new ExtError();
            final ArrayList<String> list = new ArrayList<>();
            for (int i = 1; i <= StringUtils.numToken(item.getPhones(), ";"); i++) {
                String phone = StringUtils.token(item.getPhones(), ";", i);
                result = mValidateController.validate(PhoneContactItemValidator.NAME, phone);
                if (result.getResult()) {
                    String phone1 = null;
                    if (ApplicationUtils.hasLollipop()) {
                        phone1 = PhoneNumberUtils.formatNumber(phone, Locale.getDefault().getCountry());
                    }
                    if (StringUtils.isNullOrEmpty(phone1)) {
                        list.add(phone);
                    } else {
                        list.add(phone1);
                    }
                } else {
                    err.addError(result.getSender(), result.getErrorText());
                }
            }
            if (!list.isEmpty()) {
                AdminUtils.postEvent(new ShowListDialogEvent(R.id.dialog_call_phone, item.getName(), null, list, -1, R.string.exit, true));
            } else {
                ErrorController.getInstance().onError(err);
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

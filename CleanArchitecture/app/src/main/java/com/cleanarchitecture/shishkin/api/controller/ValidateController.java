package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.data.Result;
import com.cleanarchitecture.shishkin.api.validate.IValidator;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValidateController extends AbstractController<IValidateSubscriber> implements IValidateController {

    public static final String NAME = ValidateController.class.getName();
    public static final String SUBSCRIBER_TYPE = IValidateSubscriber.class.getName();

    private Map<String, IValidator> mValidators = Collections.synchronizedMap(new ConcurrentHashMap<String, IValidator>());
    private static final String LOG_TAG = "ValidateController:";

    @Override
    public synchronized void register(final IValidateSubscriber subscriber) {
        super.register(subscriber);

        if (subscriber == null) {
            return;
        }

        final List<String> validators = subscriber.hasValidatorType();
        if (validators != null) {
            for (String name : validators) {
                checkValidator(name);
            }
        }
    }

    @Override
    public synchronized void unregister(final IValidateSubscriber subscriber) {
        super.unregister(subscriber);

        if (subscriber == null) {
            return;
        }

        final List<String> validators = subscriber.hasValidatorType();
        if (validators != null) {
            for (String name : validators) {
                if (mValidators.containsKey(name)) {
                    mValidators.remove(name);
                }
            }
        }
    }

    private synchronized void checkValidator(final String name) {
        if (!mValidators.containsKey(name)) {
            try {
                final IValidator validator = (IValidator) Class.forName(name).newInstance();
                mValidators.put(name, validator);
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            }
        }
    }

    @Override
    public Result<Boolean> validate(final String name, final Object object) {
        final Result<Boolean> result = new Result<>();

        if (StringUtils.isNullOrEmpty(name)) {
            return result.setResult(false);
        }

        checkValidator(name);

        final IValidator validator = mValidators.get(name);
        if (validator != null) {
            return validator.validate(object);
        } else {
            result.setResult(false);
            final Context context = AdminUtils.getContext();
            if (context != null) {
                result.setError(NAME, context.getString(R.string.error_validator_not_found) + ": " + name);
            }
            return result;
        }
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
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_validation);
        }
        return "Validate controller";
    }
}

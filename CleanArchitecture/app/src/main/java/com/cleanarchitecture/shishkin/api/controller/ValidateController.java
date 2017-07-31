package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.data.Result;
import com.cleanarchitecture.shishkin.api.validate.IValidator;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValidateController extends AbstractController<IValidateSubscriber> implements IValidateController {

    public static final String NAME = ValidateController.class.getName();
    public static final String SUBSCRIBER_TYPE = IValidateSubscriber.class.getName();

    private Map<String, IValidator> mValidators = Collections.synchronizedMap(new ConcurrentHashMap<String, IValidator>());

    @Override
    public synchronized void register(final IValidateSubscriber subscriber) {
        super.register(subscriber);

        if (subscriber == null) {
            return;
        }

        final IValidator validator = subscriber.getValidator();
        if (validator != null) {
            mValidators.put(subscriber.getName(), validator);
        }
    }

    @Override
    public synchronized void unregister(final IValidateSubscriber subscriber) {
        super.unregister(subscriber);

        if (subscriber == null) {
            return;
        }

        if (!StringUtils.isNullOrEmpty(subscriber.getName())) {
            mValidators.remove(subscriber.getName());
        }
    }

    @Override
    public Result<Boolean> validate(final IValidateSubscriber subscriber, final Object object) {
        final Result<Boolean> result = new Result<>();

        if (subscriber == null) {
            return result.setResult(false);
        }

        if (mValidators.containsKey(subscriber.getName())) {
            return mValidators.get(subscriber.getName()).validate(object);
        } else {
            final IValidator validator = subscriber.getValidator();
            if (validator != null) {
                mValidators.put(subscriber.getName(), validator);
                return validator.validate(object);
            } else {
                result.setResult(false);
                final Context context = AdminUtils.getContext();
                if (context != null) {
                    result.setError(NAME, context.getString(R.string.error_validator_not_found));
                }
                return result;
            }
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

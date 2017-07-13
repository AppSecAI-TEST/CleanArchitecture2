package com.cleanarchitecture.shishkin.api.controller;

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
        if (subscriber == null) {
            return;
        }

        super.register(subscriber);

        if (!StringUtils.isNullOrEmpty(subscriber.getName()) && subscriber.getValidator() != null) {
            mValidators.put(subscriber.getName(), subscriber.getValidator());
        }
    }

    @Override
    public synchronized void unregister(final IValidateSubscriber subscriber) {
        if (subscriber == null) {
            return;
        }

        if (!StringUtils.isNullOrEmpty(subscriber.getName())) {
            mValidators.remove(subscriber.getName());
        }

        super.register(subscriber);
    }

    @Override
    public boolean validate(IValidateSubscriber subscriber, Object object) {
        if (subscriber == null || object == null) {
            return false;
        }

        if (mValidators.containsKey(subscriber.getName())) {
            final IValidator validator = mValidators.get(subscriber.getName());
            return validator.execValidate(object);
        }
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return SUBSCRIBER_TYPE;
    }
}

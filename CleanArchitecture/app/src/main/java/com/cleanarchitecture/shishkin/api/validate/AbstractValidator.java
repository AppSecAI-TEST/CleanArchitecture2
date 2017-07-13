package com.cleanarchitecture.shishkin.api.validate;

import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractValidator implements IValidator {

    private Map<String, IValidator> mValidators = Collections.synchronizedMap(new ConcurrentHashMap<String, IValidator>());

    public AbstractValidator() {
        add(this);
    }

    @Override
    public boolean execValidate(Object object) {
        if (validate(object)) {
            for (IValidator validator : mValidators.values()) {
                if (!validator.validate(object)) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public Object fix(Object object) {
        return object;
    }

    @Override
    public void add(IValidator validator) {
        if (validator != null && !StringUtils.isNullOrEmpty(validator.getName())) {
            mValidators.put(validator.getName(), validator);
        }
    }
}

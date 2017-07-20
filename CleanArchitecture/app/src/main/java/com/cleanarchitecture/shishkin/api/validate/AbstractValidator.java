package com.cleanarchitecture.shishkin.api.validate;

import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractValidator<T> implements IValidator<T> {

    private Map<String, IValidator> mValidators = new HashMap<>();

    @Override
    public T fix(T object) {
        return object;
    }

    @Override
    public void add(IValidator validator) {
        if (validator != null && !StringUtils.isNullOrEmpty(validator.getName())) {
            mValidators.put(validator.getName(), validator);
        }
    }

    @Override
    public IValidator get(String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            return null;
        }

        if (mValidators.containsKey(name)) {
            return mValidators.get(name);
        }
        return null;
    }
}

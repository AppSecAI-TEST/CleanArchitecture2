package com.cleanarchitecture.shishkin.api.validate;

import com.cleanarchitecture.shishkin.api.data.Result;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractValidator implements IValidator {

    private Map<String, IValidator> mValidators = Collections.synchronizedMap(new ConcurrentHashMap<String, IValidator>());

    @Override
    public Result<Boolean> execValidate(Object object) {
        Result<Boolean> result = validate(object);
        if (result.getResult()) {
            for (IValidator validator : mValidators.values()) {
                result = validator.validate(object);
                if (!result.getResult()) {
                    return result;
                }
            }
        }
        return result;
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

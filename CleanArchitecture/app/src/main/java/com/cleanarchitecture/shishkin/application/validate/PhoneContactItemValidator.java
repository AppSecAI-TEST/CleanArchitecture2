package com.cleanarchitecture.shishkin.application.validate;

import com.cleanarchitecture.shishkin.api.data.Result;
import com.cleanarchitecture.shishkin.api.validate.AbstractValidator;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

public class PhoneContactItemValidator extends AbstractValidator {

    public static final String NAME = PhoneContactItemValidator.class.getName();

    public PhoneContactItemValidator() {
        add(new PhoneValidator());
    }

    @Override
    public Result<Boolean> validate(final Object object) {
        Result<Boolean> result = new Result<>();

        if (object == null) {
            return result.setResult(false);
        }

        if (object instanceof PhoneContactItem) {
            final PhoneContactItem item = (PhoneContactItem) object;
            final String phones = item.getPhones();
            if (!StringUtils.isNullOrEmpty(phones)) {
                return result.setResult(true);
            } else {
                return result.setResult(false).setError(NAME, "Нет телефонов");
            }
        } else if (object instanceof String) {
            return get(PhoneValidator.NAME).validate(object);
        }
        return result.setResult(true);
    }

    @Override
    public String getName() {
        return NAME;
    }
}

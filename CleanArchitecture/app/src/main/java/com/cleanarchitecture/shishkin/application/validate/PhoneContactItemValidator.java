package com.cleanarchitecture.shishkin.application.validate;

import com.cleanarchitecture.shishkin.api.validate.AbstractValidator;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

public class PhoneContactItemValidator extends AbstractValidator {

    public static final String NAME = PhoneContactItemValidator.class.getName();

    public PhoneContactItemValidator() {
    }

    @Override
    public boolean validate(final Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof PhoneContactItem) {
            final PhoneContactItem item = (PhoneContactItem) object;
            final String phones = item.getPhones();
            if (StringUtils.isNullOrEmpty(phones)) {
                return false;
            }
        } else if (object instanceof String) {
            final String phone = (String) object;
            if (StringUtils.isNullOrEmpty(phone)) {
                return false;
            } else if (phone.length() < 5) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}

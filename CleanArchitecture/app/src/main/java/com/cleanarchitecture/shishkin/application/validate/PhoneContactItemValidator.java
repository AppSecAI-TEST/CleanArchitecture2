package com.cleanarchitecture.shishkin.application.validate;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.repository.data.ExtError;
import com.cleanarchitecture.shishkin.api.repository.data.Result;
import com.cleanarchitecture.shishkin.api.validate.AbstractValidator;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

public class PhoneContactItemValidator extends AbstractValidator {

    public static final String NAME = PhoneContactItemValidator.class.getName();

    @Override
    public Result<Boolean> validate(final Object object) {
        final Result<Boolean> result = new Result<>();
        result.setResult(false);

        if (object == null) {
            return result;
        }

        if (object instanceof PhoneContactItem) {
            final PhoneContactItem item = (PhoneContactItem) object;
            final String phones = item.getPhones();
            if (!StringUtils.isNullOrEmpty(phones)) {
                result.setResult(true);
            }
        } else if (object instanceof String) {
            final String phone = (String) object;
            if (!StringUtils.isNullOrEmpty(phone)) {
                final int length = StringUtils.getDigits(phone).length();
                if (length == 7 || length == 11) {
                    result.setResult(true);
                } else if (length == 3) {
                    result.setResult(true);
                } else if (length > 11) {
                    result.setResult(false);
                    final Context context = AdminUtils.getContext();
                    if (context != null) {
                        result.setError(new ExtError().setErrorText(NAME, context.getString(R.string.error_phone_max_length, phone)));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String getName() {
        return NAME;
    }
}

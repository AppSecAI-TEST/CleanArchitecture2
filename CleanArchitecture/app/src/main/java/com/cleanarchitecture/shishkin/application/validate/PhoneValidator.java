package com.cleanarchitecture.shishkin.application.validate;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.data.Result;
import com.cleanarchitecture.shishkin.api.validate.AbstractValidator;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

public class PhoneValidator extends AbstractValidator<String> {

    public static final String NAME = PhoneValidator.class.getName();

    @Override
    public Result<Boolean> validate(final String phone) {
        final Result<Boolean> result = new Result<>();

        if (StringUtils.isNullOrEmpty(phone)) {
            return result.setResult(false);
        }

        final int length = StringUtils.getDigits(phone).length();
        if (length == 7 || length == 11) {
            return result.setResult(true);
        } else if (length == 3) {
            return result.setResult(true);
        } else if (length > 11) {
            result.setResult(false);
            final Context context = AdminUtils.getContext();
            if (context != null) {
                return result.setError(NAME, context.getString(R.string.error_phone_max_length, phone));
            }
            return result;
        } else if (length < 3) {
            final Context context = AdminUtils.getContext();
            if (context != null) {
                return result.setError(NAME, context.getString(R.string.error_phone_min_length, phone));
            }
        }
        return result.setResult(false);
    }

    @Override
    public String getName() {
        return NAME;
    }
}

package com.cleanarchitecture.shishkin.api.mail;

import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractMail implements IMail {

    private String mAddress;
    private String mSender;
    private List<String> mCopyTo = new LinkedList<>();
    private long mId = 0;
    private long mEndTime = -1;

    public AbstractMail(final String address) {
        mAddress = address;
    }

    @Override
    public String getAddress() {
        return mAddress;
    }

    @Override
    public IMail setAddress(final String address) {
        this.mAddress = address;
        return this;
    }

    @Override
    public List<String> getCopyTo() {
        return mCopyTo;
    }

    @Override
    public IMail setCopyTo(final List<String> copyTo) {
        this.mCopyTo = copyTo;
        return this;
    }

    @Override
    public boolean contains(final String address) {
        if (StringUtils.isNullOrEmpty(address)) {
            return false;
        }

        if (address.equalsIgnoreCase(mAddress)) {
            return true;
        }

        for (String copyto : mCopyTo) {
            if (copyto.equalsIgnoreCase(address)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Long getId() {
        return mId;
    }

    @Override
    public IMail setId(final Long id) {
        if (id != null) {
            this.mId = id;
        }
        return this;
    }

    @Override
    public long getEndTime() {
        return mEndTime;
    }

    @Override
    public IMail setEndTime(final long endTime) {
        mEndTime = endTime;
        return this;
    }

    @Override
    public boolean isCheckDublicate() {
        return false;
    }

    @Override
    public String getSender() {
        return mSender;
    }

    @Override
    public IMail setSender(final String sender) {
        this.mSender = sender;
        return this;
    }

    @Override
    public boolean isSticky() {
        return false;
    }

}

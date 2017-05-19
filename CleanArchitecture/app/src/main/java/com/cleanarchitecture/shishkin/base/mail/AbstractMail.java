package com.cleanarchitecture.shishkin.base.mail;

import com.cleanarchitecture.shishkin.base.controller.EventController;
import com.cleanarchitecture.shishkin.base.controller.IEventVendor;
import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.event.IEvent;
import com.cleanarchitecture.shishkin.base.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMail implements IMail, IEventVendor {

    private String mAddress;
    private String mSender;
    private List<String> mCopyTo = new ArrayList<>();
    private long mId = 0;

    public AbstractMail(final String address) {
        mAddress = address;
    }

    public AbstractMail(final String address, final List<String> copyTo) {
        mAddress = address;
        mCopyTo = copyTo;
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

    public abstract void read(IMailSubscriber subscriber);

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

    public abstract IMail copy();

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

    @Override
    public void postEvent(IEvent event) {
        if (event != null) {
            EventController.getInstance().post(event);
        }
    }
}

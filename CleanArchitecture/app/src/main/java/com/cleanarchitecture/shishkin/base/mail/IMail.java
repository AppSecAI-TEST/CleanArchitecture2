package com.cleanarchitecture.shishkin.base.mail;

import com.cleanarchitecture.shishkin.base.controller.IMailSubscriber;
import com.cleanarchitecture.shishkin.base.controller.ISubscriber;

import java.util.List;

public interface IMail extends ISubscriber {

    void read(IMailSubscriber subscriber);

    boolean contains(String address);

    Long getId();

    IMail setId(Long id);

    IMail copy();

    List<String> getCopyTo();

    IMail setCopyTo(List<String> copyTo);

    String getAddress();

    IMail setAddress(String address);

    boolean isCheckDublicate();

    String getSender();

    IMail setSender(String sender);

    boolean isSticky();

    long getEndTime();

    IMail setEndTime(long keepAliveTime);

}

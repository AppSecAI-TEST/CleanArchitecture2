package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.mail.IMail;
import com.cleanarchitecture.shishkin.common.state.ViewStateObserver;
import com.cleanarchitecture.shishkin.common.task.BaseAsyncTask;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MailController extends AbstractController<IMailSubscriber> implements IMailController {

    public static final String NAME = MailController.class.getName();
    public static final String SUBSCRIBER_TYPE = IMailSubscriber.class.getName();
    private Map<Long, IMail> mMail = Collections.synchronizedMap(new ConcurrentHashMap<Long, IMail>());
    private AtomicLong mId = new AtomicLong(0L);

    public MailController() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return SUBSCRIBER_TYPE;
    }

    @Override
    public synchronized List<IMail> getMail(final IMailSubscriber subscriber) {
        if (subscriber != null) {
            if (mMail.isEmpty()) {
                return new ArrayList<>();
            }

            // удаляем старые письма
            final String name = subscriber.getName();
            final long currentTime = System.currentTimeMillis();
            final ITransformDataModule module = AdminUtils.getTransformDataModule();
            final List<IMail> list = module.filter(mMail.values(), mail -> (mail.contains(name) && mail.getEndTime() != -1 && mail.getEndTime() < currentTime)).toList();
            if (!list.isEmpty()) {
                for (IMail mail : list) {
                    mMail.remove(mail.getId());
                }
            }

            if (mMail.isEmpty()) {
                return new ArrayList<>();
            }

            final Comparator<IMail> byId = (left, right) -> left.getId().compareTo(right.getId());
            return module.filter(mMail.values(), mail -> mail.contains(name) && (mail.getEndTime() == -1 || (mail.getEndTime() != -1 && mail.getEndTime() > currentTime))).sorted(byId).toList();
        }
        return new ArrayList<>();
    }

    @Override
    public synchronized void clearMail(final IMailSubscriber subscriber) {
        if (subscriber != null) {
            if (mMail.isEmpty()) {
                return;
            }

            final String name = subscriber.getName();
            final ITransformDataModule module = AdminUtils.getTransformDataModule();
            final List<IMail> list = module.filter(mMail.values(), mail -> mail.contains(name)).toList();
            if (!list.isEmpty()) {
                for (IMail mail : list) {
                    mMail.remove(mail.getId());
                }
            }
        }
    }

    @Override
    public synchronized void addMail(final IMail mail) {
        if (mail != null) {
            final List<String> list = mail.getCopyTo();
            list.add(mail.getAddress());
            for (String address : list) {
                final long id = mId.incrementAndGet();
                final IMail newMail = mail.copy();
                newMail.setId(id);
                newMail.setAddress(address);
                newMail.setCopyTo(new ArrayList<>());

                if (!mail.isCheckDublicate()) {
                    mMail.put(id, newMail);
                } else {
                    removeDublicate(newMail);
                    mMail.put(id, newMail);
                }

                checkAddMailSubscriber(address);
            }
        }
    }

    private synchronized void checkAddMailSubscriber(final String address) {
        if (StringUtils.isNullOrEmpty(address)) {
            return;
        }

        for (WeakReference<IMailSubscriber> reference : getSubscribers().values()) {
            if (reference != null && reference.get() != null) {
                final IMailSubscriber subscriber = reference.get();
                if (address.equalsIgnoreCase(subscriber.getName())) {
                    if (subscriber.getState() == ViewStateObserver.STATE_RESUME) {
                        new BaseAsyncTask() {
                            @Override
                            public void run() {
                                AdminUtils.readMail(subscriber);
                            }
                        }.execute();
                    }
                }
            }
        }
    }

    private synchronized void removeDublicate(final IMail mail) {
        if (mail != null) {
            for (IMail tmpMail : mMail.values()) {
                if (tmpMail.getName().equals(mail.getName()) && tmpMail.getAddress().equals(mail.getAddress())) {
                    removeMail(tmpMail);
                }
            }
        }
    }

    @Override
    public synchronized void removeMail(final IMail mail) {
        if (mail != null) {
            if (mMail.containsKey(mail.getId())) {
                mMail.remove(mail.getId());
            }
        }
    }

    @Override
    public synchronized void clearMail() {
        mMail.clear();
    }

    @Override
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_mail);
        }
        return "Mail controller";
    }

    @Override
    public boolean isPersistent() {
        return true;
    }


}

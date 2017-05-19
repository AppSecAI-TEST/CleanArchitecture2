package com.cleanarchitecture.shishkin.base.controller;

import com.annimon.stream.Stream;
import com.cleanarchitecture.shishkin.base.lifecycle.Lifecycle;
import com.cleanarchitecture.shishkin.base.mail.IMail;
import com.github.snowdream.android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MailController extends AbstractController implements IMailController {

    private static final String NAME = "MailController";
    private static volatile MailController sInstance;
    private Map<String, WeakReference<IMailSubscriber>> mSubscribers = Collections.synchronizedMap(new HashMap<String, WeakReference<IMailSubscriber>>());
    private Map<Long, IMail> mMail = Collections.synchronizedMap(new HashMap<Long, IMail>());
    private AtomicLong mId = new AtomicLong(0L);

    public static synchronized void instantiate() {
        if (sInstance == null) {
            synchronized (MailController.class) {
                if (sInstance == null) {
                    sInstance = new MailController();
                }
            }
        }
    }

    public static synchronized MailController getInstance() {
        instantiate();
        return sInstance;
    }

    private MailController() {
        mSubscribers = Collections.synchronizedMap(new HashMap<String, WeakReference<IMailSubscriber>>());
        mMail = Collections.synchronizedMap(new HashMap<Long, IMail>());
    }

    @Override
    public String getName() {
        return NAME;
    }

    private synchronized void checkNullSubscriber() {
        for (Map.Entry<String, WeakReference<IMailSubscriber>> entry : mSubscribers.entrySet()) {
            if (entry.getValue().get() == null) {
                mSubscribers.remove(entry.getKey());
            }
        }
    }

    @Override
    public synchronized void register(final IMailSubscriber subscriber) {
        if (subscriber != null) {
            checkNullSubscriber();

            mSubscribers.put(subscriber.getName(), new WeakReference<>(subscriber));
        }
    }

    @Override
    public synchronized void unregister(final IMailSubscriber subscriber) {
        if (subscriber != null) {
            if (mSubscribers.containsKey(subscriber.getName())) {
                for (IMail mail : mMail.values()) {
                    if (mail.contains(subscriber.getName())) {
                        if (!mail.isSticky()) {
                            mMail.remove(mail.getId());
                        }
                    }
                }

                mSubscribers.remove(subscriber.getName());
            }

            checkNullSubscriber();
        }
    }

    @Override
    public synchronized List<IMail> getMail(final IMailSubscriber subscriber) {
        if (subscriber != null) {
            if (mMail.isEmpty()) {
                return new ArrayList<>();
            }
            final String name = subscriber.getName();
            final Comparator<IMail> byId = (left, right) -> left.getId().compareTo(right.getId());
            return Stream.of(mMail.values()).filter(mail -> mail.contains(name)).sorted(byId).toList();
        }
        return new ArrayList<>();
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

                try {
                    for (WeakReference<IMailSubscriber> ref : mSubscribers.values()) {
                        if (ref != null && ref.get() != null) {
                            final IMailSubscriber subscriber = ref.get();
                            if (address.equalsIgnoreCase(subscriber.getName())) {
                                if (subscriber.getState() == Lifecycle.STATE_RESUME) {
                                    subscriber.readMail();
                                }

                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(NAME, e.getMessage());
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

}

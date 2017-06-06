package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.event.IEvent;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Контроллер шины событий приложения
 */
public class EventBusController implements IEventBusController {

    public static final int MAX_RANK = 10;
    public static final int HIGH_RANK = 8;
    public static final int MIDDLE_RANK = 5;
    public static final int LOW_RANK = 2;
    public static final int MIN_RANK = 0;

    public static final String NAME = "EventController";
    public static final String SUBSCRIBER_TYPE = "IEventbusSubscriber";
    private static volatile EventBusController sInstance;

    public static EventBusController getInstance() {
        if (sInstance == null) {
            synchronized (EventBusController.class) {
                if (sInstance == null) {
                    sInstance = new EventBusController();
                }
            }
        }
        return sInstance;
    }

    private EventBusController() {
        EventBus.getDefault();
    }

    /**
     * добавить событие
     *
     * @param event событие
     */
    @Override
    public synchronized void post(final IEvent event) {
        if (event != null) {
            EventBus.getDefault().post(event);
        }
    }

    /**
     * добавить постоянное событие
     *
     * @param event событие
     */
    @Override
    public synchronized void postSticky(final IEvent event) {
        if (event != null) {
            EventBus.getDefault().postSticky(event);
        }
    }

    /**
     * удалить постоянное событие
     *
     * @param event the event
     */
    @Override
    public synchronized void removeSticky(final IEvent event) {
        if (event != null) {
            EventBus.getDefault().removeStickyEvent(event.getClass());
        }
    }

    /**
     * Зарегестрировать подписчика шины событий
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void register(final Object subscriber) {
        if (subscriber != null) {
            if (!EventBus.getDefault().isRegistered(subscriber)) {
                EventBus.getDefault().register(subscriber);
            }
        }
    }

    /**
     * отключить подписчика шины событий
     *
     * @param subscriber подписчик
     */
    @Override
    public synchronized void unregister(final Object subscriber) {
        if (subscriber != null) {
            if (EventBus.getDefault().isRegistered(subscriber)) {
                EventBus.getDefault().unregister(subscriber);
            }
        }
    }

    @Override
    public void setCurrentSubscriber(Object subscriber) {
    }

    @Override
    public Object getCurrentSubscriber() {
        return null;
    }

    @Override
    public Map<String, WeakReference<Object>> getSubscribers() {
        return null;
    }

    @Override
    public Object getSubscriber() {
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return SUBSCRIBER_TYPE;
    }

}

package com.cleanarchitecture.shishkin.api.controller;

import android.content.Context;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.event.IEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Контроллер шины событий приложения
 */
public class EventBusController implements IEventBusController {

    public static final int MAX_RANK = 10;
    public static final int HIGH_RANK = 8;
    public static final int MIDDLE_RANK = 5;
    public static final int LOW_RANK = 2;
    public static final int MIN_RANK = 0;

    public static final String NAME = EventBusController.class.getName();
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
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public void onUnRegisterModule() {
    }

    @Override
    public String getDescription() {
        final Context context = ApplicationController.getInstance();
        if (context != null) {
            return context.getString(R.string.module_event);
        }
        return "Event bus controller";
    }

}

package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.event.IEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Контроллер шины событий приложения
 */
public class EventController extends AbstractController implements IEventController {
    public static final int MAX_RANK = 10;
    public static final int HIGH_RANK = 8;
    public static final int MIDDLE_RANK = 5;
    public static final int LOW_RANK = 2;
    public static final int MIN_RANK = 0;

    private static final String NAME = "EventController";

    private static volatile EventController sInstance;

    public static void instantiate() {
        if (sInstance == null) {
            synchronized (EventController.class) {
                if (sInstance == null) {
                    sInstance = new EventController();
                }
            }
        }
    }

    public static EventController getInstance() {
        instantiate();
        return sInstance;
    }

    private EventController() {
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

}

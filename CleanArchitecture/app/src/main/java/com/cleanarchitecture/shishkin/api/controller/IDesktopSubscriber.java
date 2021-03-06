package com.cleanarchitecture.shishkin.api.controller;

public interface IDesktopSubscriber extends ISubscriber {

    /**
     * Получить порядок элементов рабочего стола по умолчанию
     *
     * @return порядок элементов рабочего стола
     */
    String getDefaultDesktopOrder();

    /**
     * Получить имя порядка элементов рабочего стола по умолчанию
     *
     * @return имя порядка элементов рабочего стола
     */
    String getDesktopOrderName();
}

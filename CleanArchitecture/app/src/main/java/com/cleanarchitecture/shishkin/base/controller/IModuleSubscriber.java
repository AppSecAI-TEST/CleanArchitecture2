package com.cleanarchitecture.shishkin.base.controller;

import java.util.List;

public interface IModuleSubscriber extends ISubscriber {
    List<String> hasSubscriberType();
}

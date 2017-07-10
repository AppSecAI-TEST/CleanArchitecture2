package com.cleanarchitecture.shishkin.api.controller;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;

import java.util.List;

public class TransformDataModule implements ITransformDataModule {

    public static final String NAME = TransformDataModule.class.getName();

    public TransformDataModule() {
    }

    @Override
    public <T> List<T> filter(final List<T> list, final Predicate<? super T> predicate) {
        return Stream.of(list).filter(predicate).toList();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return null;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void onUnRegister() {
    }
}

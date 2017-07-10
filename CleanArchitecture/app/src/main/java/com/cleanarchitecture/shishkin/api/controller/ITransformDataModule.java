package com.cleanarchitecture.shishkin.api.controller;

import com.annimon.stream.function.Predicate;

import java.util.List;

public interface ITransformDataModule extends IModule {

    <T> List<T> filter(final List<T> list, final Predicate<? super T> predicate);
}

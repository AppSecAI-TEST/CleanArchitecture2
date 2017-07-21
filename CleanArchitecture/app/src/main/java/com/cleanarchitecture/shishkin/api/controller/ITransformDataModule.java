package com.cleanarchitecture.shishkin.api.controller;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.cleanarchitecture.shishkin.common.collection.GenericCollection;
import com.cleanarchitecture.shishkin.common.collection.GenericMap;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.Collection;

public interface ITransformDataModule extends IModule {

    <T> Stream<T> filter(final Collection<T> list, final Predicate<? super T> predicate);

    <T> T fromJson(Object jsonObject, Class<T> clazz);

    <T> T fromJson(Object jsonObject, TypeToken<T> typeToken);

    <T, Collection, Value> T fromJsonCollection(Object jsonObject, GenericCollection<Collection, Value> genericCollection);

    <T, Map, Key, Value> T fromJsonMap(Object jsonObject, GenericMap<Map, Key, Value> genericMap);

    <T> Serializable toJson(final T obj);

    <T> Serializable toJson(final T obj, final TypeToken<T> typeToken);

}

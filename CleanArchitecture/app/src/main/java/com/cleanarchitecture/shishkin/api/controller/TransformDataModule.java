package com.cleanarchitecture.shishkin.api.controller;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.cleanarchitecture.shishkin.common.collections.GenericCollection;
import com.cleanarchitecture.shishkin.common.collections.GenericMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class TransformDataModule extends AbstractModule implements ITransformDataModule {

    public static final String NAME = TransformDataModule.class.getName();
    public static final String LOG_TAG = "TransformDataModule:";

    private GsonBuilder mGsonBuilder;
    private Gson mGson;

    public TransformDataModule() {
        mGsonBuilder = new GsonBuilder();
        mGson = mGsonBuilder.create();
    }

    @Override
    public <T> List<T> filter(final List<T> list, final Predicate<? super T> predicate) {
        return Stream.of(list).filter(predicate).toList();
    }

    private synchronized String getJsonString(final Object jsonObject, final Gson gson) {
        String jsonString;
        if (!String.class.isInstance(jsonObject)) {
            jsonString = gson.toJson(jsonObject);
        } else {
            jsonString = (String) jsonObject;
        }
        return jsonString;
    }

    public synchronized <T> T parse(Object jsonObject, Class<T> clazz) {
        if (jsonObject == null && clazz == null) {
            return null;
        }

        T object = null;
        try {
            String jsonString = getJsonString(jsonObject, mGson);
            object = mGson.fromJson(jsonString, clazz);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
        return object;
    }

    public synchronized <T> T parse(Object jsonObject, TypeToken<T> typeToken) {
        if (jsonObject == null && typeToken == null) {
            return null;
        }

        T object = null;
        try {
            String jsonString = getJsonString(jsonObject, mGson);
            object = mGson.fromJson(jsonString, typeToken.getType());
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
        return object;
    }

    public synchronized <T, Collection, Value> T parseCollection(Object jsonObject, GenericCollection<Collection, Value> genericCollection) {
        if (jsonObject == null && genericCollection == null) {
            return null;
        }

        T object = null;
        try {
            String jsonString = getJsonString(jsonObject, mGson);
            object = mGson.fromJson(jsonString, genericCollection);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
        return object;
    }

    public synchronized <T, Map, Key, Value> T parseMap(Object jsonObject, GenericMap<Map, Key, Value> genericMap) {
        if (jsonObject == null || genericMap == null) {
            return null;
        }

        T object = null;
        try {
            String jsonString = getJsonString(jsonObject, mGson);
            object = mGson.fromJson(jsonString, genericMap);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
        return object;
    }


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSubscriberType() {
        return null;
    }

}

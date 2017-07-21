package com.cleanarchitecture.shishkin.api.controller;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.cleanarchitecture.shishkin.common.collection.GenericCollection;
import com.cleanarchitecture.shishkin.common.collection.GenericMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.Collection;

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
    public <T> Stream<T> filter(final Collection<T> list, final Predicate<? super T> predicate) {
        return Stream.of(list).filter(predicate);
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

    @Override
    public synchronized <T> T fromJson(Object jsonObject, Class<T> clazz) {
        if (jsonObject == null && clazz == null) {
            return null;
        }

        try {
            final String jsonString = getJsonString(jsonObject, mGson);
            return mGson.fromJson(jsonString, clazz);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
        return null;
    }

    @Override
    public synchronized <T> T fromJson(Object jsonObject, TypeToken<T> typeToken) {
        if (jsonObject == null && typeToken == null) {
            return null;
        }

        try {
            final String jsonString = getJsonString(jsonObject, mGson);
            return mGson.fromJson(jsonString, typeToken.getType());
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
        return null;
    }

    @Override
    public synchronized <T, Collection, Value> T fromJsonCollection(Object jsonObject, GenericCollection<Collection, Value> genericCollection) {
        if (jsonObject == null && genericCollection == null) {
            return null;
        }

        try {
            final String jsonString = getJsonString(jsonObject, mGson);
            return mGson.fromJson(jsonString, genericCollection);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
        return null;
    }

    @Override
    public synchronized <T, Map, Key, Value> T fromJsonMap(Object jsonObject, GenericMap<Map, Key, Value> genericMap) {
        if (jsonObject == null || genericMap == null) {
            return null;
        }

        try {
            String jsonString = getJsonString(jsonObject, mGson);
            return mGson.fromJson(jsonString, genericMap);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
        return null;
    }

    @Override
    public synchronized <T> Serializable toJson(final T obj) {
        try {
            return mGson.toJson(obj);
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
        return null;
    }

    @Override
    public synchronized <T> Serializable toJson(final T obj, final TypeToken<T> typeToken) {
        if (obj == null || typeToken == null) {
            return null;
        }
        //use example : type = new com.google.gson.reflect.TypeToken<List<ContactItem>>(){}.getType()
        try {
            return mGson.toJson(obj, typeToken.getType());
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        }
        return null;
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

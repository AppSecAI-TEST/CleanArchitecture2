package com.cleanarchitecture.shishkin.base.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import java.lang.reflect.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SerializableUtil {

    private SerializableUtil() {
    }

    public static <T> Serializable toSerializable(List<T> list) {
        if (list == null) {
            return null;
        }

        final LinkedList<T> linkedList = new LinkedList<>();
        linkedList.addAll(list);
        return linkedList;
    }

    public static <T> List<T> serializableToList(Serializable value) {
        if (value == null) {
            return null;
        }

        if (value instanceof LinkedList) {
            LinkedList<T> items = (LinkedList) value;
            final List<T> list = new ArrayList<>();
            list.addAll(items);
            return list;
        } else if (value instanceof ArrayList) {
            return (ArrayList) value;
        }
        return null;
    }

    public static <T> Serializable toJson(final T obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T fromJson(final String json, final Class<T> cl) {
        return JSON.parseObject(json, cl);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> type) {
        //use example : List<VO> list = JSON.parseObject("...", new TypeReference<List<VO>>() {});
        return JSON.parseObject(json, type);
    }

    public static <T> Serializable toJson(final T obj, Type type) {
        //use example : type = new com.google.gson.reflect.TypeToken<List<ContactItem>>(){}.getType()
        final Gson gson = new Gson();
        return gson.toJson(obj, type);
    }

    public static <T> T fromJson(final String json, Type type) {
        // use example : type = new com.google.gson.reflect.TypeToken<List<ContactItem>>(){}.getType()
        final Gson gson = new Gson();
        return gson.fromJson(json, type);
    }
}

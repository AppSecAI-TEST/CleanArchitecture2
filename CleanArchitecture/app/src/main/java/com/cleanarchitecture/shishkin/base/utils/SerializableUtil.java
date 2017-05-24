package com.cleanarchitecture.shishkin.base.utils;

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

}

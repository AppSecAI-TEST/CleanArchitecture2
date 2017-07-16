package com.cleanarchitecture.shishkin.common.collection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericCollection<Collection, Value> implements ParameterizedType {

    private final Class<Collection> mCollectionClass;
    private final Class<Value> mValueClass;

    public GenericCollection(final Class<Collection> collectionClass, Class<Value> valueClass) {
        mCollectionClass = collectionClass;
        mValueClass = valueClass;
    }

    public Type[] getActualTypeArguments() {
        return new Type[]{mValueClass};
    }

    public Type getRawType() {
        return mCollectionClass;
    }

    public Type getOwnerType() {
        return null;
    }

}
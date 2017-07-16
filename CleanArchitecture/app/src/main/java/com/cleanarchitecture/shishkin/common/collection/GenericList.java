package com.cleanarchitecture.shishkin.common.collection;

import java.util.List;

public class GenericList<T> extends GenericCollection {

    public GenericList(Class<T> aclass) {
        super(List.class, aclass);
    }
}

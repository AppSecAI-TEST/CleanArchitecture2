package com.cleanarchitecture.shishkin.common.collection;

import java.util.LinkedList;

public class GenericLinkedList<T> extends GenericCollection {

    public GenericLinkedList(Class<T> aclass) {
        super(LinkedList.class, aclass);
    }
}

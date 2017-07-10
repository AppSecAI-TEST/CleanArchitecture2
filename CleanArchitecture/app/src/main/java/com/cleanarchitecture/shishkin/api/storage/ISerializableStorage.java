package com.cleanarchitecture.shishkin.api.storage;

import com.cleanarchitecture.shishkin.api.controller.ISubscriber;

import java.io.Serializable;
import java.util.List;

public interface ISerializableStorage extends ISubscriber, IStorage<Serializable> {
}

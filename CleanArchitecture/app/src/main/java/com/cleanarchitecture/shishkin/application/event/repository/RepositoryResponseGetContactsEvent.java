package com.cleanarchitecture.shishkin.application.event.repository;

import com.cleanarchitecture.shishkin.api.event.repository.RepositoryResponseEvent;
import com.cleanarchitecture.shishkin.application.data.item.PhoneContactItem;

import java.util.List;

public class RepositoryResponseGetContactsEvent extends RepositoryResponseEvent<List<PhoneContactItem>> {
}

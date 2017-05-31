package com.cleanarchitecture.shishkin.application.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.cleanarchitecture.shishkin.application.database.item.Contact;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM Contact")
    List<Contact> getAll();

    @Query("SELECT * FROM Contact WHERE RowId = :id")
    Contact get(int id);
}

package com.cleanarchitecture.shishkin.application.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.cleanarchitecture.shishkin.application.database.item.Contact;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.ROLLBACK;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM Contact")
    List<Contact> get();

    @Query("SELECT * FROM Contact WHERE RowId = :id")
    Contact get(String id);

    @Insert(onConflict = ROLLBACK)
    void insert(Contact contact);

    @Delete
    void delete(Contact contact);

    @Query("DELETE FROM Contact")
    void delete();

    @Query("SELECT count(1) FROM Contact WHERE RowId = :id")
    int getCount(String id);
}

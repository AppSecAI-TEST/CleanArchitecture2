package com.cleanarchitecture.shishkin.application.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.cleanarchitecture.shishkin.application.database.dao.ContactDao;
import com.cleanarchitecture.shishkin.application.database.item.Contact;

@Database(entities = {Contact.class}, version = 1)
public abstract class CleanArchitectureDb extends RoomDatabase {
    public static final String NAME = "clean_architecture_db.db";

    public abstract ContactDao contactDao();

}
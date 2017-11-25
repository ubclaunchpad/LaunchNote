package com.example.ubclaunchpad.launchnote.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.ubclaunchpad.launchnote.models.Class;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ClassDao {

    // find by ID query
    // retrieve the Class with the correct ID from the database
    @Query("SELECT * FROM Class WHERE id = :id")
    Flowable<List<Class>> findById(String id);

    // insert any number of Classes into the database
    // if any IDs conflict with a previously inserted Class, replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Class... classes);

    // insert a single Class into the database
    // if the ID conflicts with a previously inserted PicNote, replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Class classToInsert);

    // delete a Class from the database
    @Delete
    void delete(Class classToDelete);

    // retrieve all Classes from the database
    @Query("SELECT * FROM Class")
    Flowable<List<Class>> loadAll();


}
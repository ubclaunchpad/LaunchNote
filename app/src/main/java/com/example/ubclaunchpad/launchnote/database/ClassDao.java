package com.example.ubclaunchpad.launchnote.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.example.ubclaunchpad.launchnote.models.LaunchNoteClass;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ClassDao {

    // find by ID query
    // retrieve the LaunchNoteClass with the correct ID from the database
    @Query("SELECT * FROM LaunchNoteClass WHERE id = :id")
    Flowable<List<LaunchNoteClass>> findById(Integer id);

    // insert any number of Classes into the database
    // if any IDs conflict with a previously inserted LaunchNoteClass, replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(LaunchNoteClass... classes);

    // insert a single LaunchNoteClass into the database
    // if the ID conflicts with a previously inserted PicNote, replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LaunchNoteClass classToInsert);

    // delete a LaunchNoteClass from the database
    @Delete
    void delete(LaunchNoteClass classToDelete);

    // retrieve all Classes from the database
    @Query("SELECT * FROM LaunchNoteClass")
    Flowable<List<LaunchNoteClass>> loadAll();
}
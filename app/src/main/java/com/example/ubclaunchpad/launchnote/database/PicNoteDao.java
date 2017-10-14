package com.example.ubclaunchpad.launchnote.database;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.ubclaunchpad.launchnote.models.PicNote;

import java.util.List;

/**
 * Data Access Object for PicNote
 * PicNote is the name of the table
 */
@Dao
public interface PicNoteDao {

    // find by ID query
    // retrieve the PicNote with the correct ID from the database
    @Query("SELECT * FROM PicNote WHERE id = :id")
    List<PicNote> findById(String id);

    // insert any number of PicNotes into the database
    // if any IDs conflict with a previously inserted PicNote, replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(PicNote... picNotes);

    // insert a single PicNote into the database
    // if the ID conflicts with a previously inserted PicNote, replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PicNote picNote);

    // delete a PicNote from the database
    @Delete
    void delete(PicNote picNote);

    // retrieve all PicNotes from the database
    @Query("SELECT * FROM PicNote")
    List<PicNote> loadAll();
}

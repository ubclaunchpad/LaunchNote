package com.example.sherryuan.launchnote.database;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.sherryuan.launchnote.models.PicNote;

import java.util.List;

/**
 * Data Access Object for PicNote
 * PicNote is the name of the table
 */
@Dao
public interface PicNoteDao {

    @Query("SELECT * FROM PicNote WHERE id = :id")
    List<PicNote> findById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(PicNote... picNotes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PicNote picNote);

    @Delete
    void delete(PicNote picNote);

    @Query("SELECT * FROM PicNote")
    List<PicNote> loadAll();
}

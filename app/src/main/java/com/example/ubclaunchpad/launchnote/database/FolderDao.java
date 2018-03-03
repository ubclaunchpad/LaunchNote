package com.example.ubclaunchpad.launchnote.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.ubclaunchpad.launchnote.models.Folder;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface FolderDao {

    // find by ID query
    // retrieve the Folder with the correct ID from the database
    @Query("SELECT * FROM Folder WHERE id = :id")
    Flowable<List<Folder>> findById(String id);

    // insert any number of Projects into the database
    // if any IDs conflict with a previously inserted Folder, replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Folder... folders);

    // insert a single Folder into the database
    // if the ID conflicts with a previously inserted Folder, replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Folder folder);

    // delete a Folder from the database
    @Delete
    void delete(Folder folder);

    // retrieve all Projects from the database
    @Query("SELECT * FROM Folder")
    Flowable<List<Folder>> loadAll();


}

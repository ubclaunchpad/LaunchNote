package com.example.ubclaunchpad.launchnote.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.example.ubclaunchpad.launchnote.models.Project;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ProjectDao {

    // find by ID query
    // retrieve the Project with the correct ID from the database
    @Query("SELECT * FROM Project WHERE id = :id")
    Flowable<List<Project>> findById(Integer id);

    // insert any number of Projects into the database
    // if any IDs conflict with a previously inserted Project, replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Project... projects);

    // insert a single Project into the database
    // if the ID conflicts with a previously inserted Project, replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Project project);

    // delete a Project from the database
    @Delete
    void delete(Project project);

    // retrieve all Projects from the database
    @Query("SELECT * FROM Project")
    Flowable<List<Project>> loadAll();


}

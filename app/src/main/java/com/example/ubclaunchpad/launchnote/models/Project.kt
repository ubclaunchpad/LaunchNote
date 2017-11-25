package com.example.ubclaunchpad.launchnote.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.example.ubclaunchpad.launchnote.database.Converters

/**
 * Model class for representing a project
 */
@Entity
@TypeConverters(Converters::class)
data class Project(
        var description: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var subProjectIds: MutableList<Int> = mutableListOf()
    var picNoteIds: MutableList<Int> = mutableListOf()
}
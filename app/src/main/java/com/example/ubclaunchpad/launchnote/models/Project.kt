package com.example.ubclaunchpad.launchnote.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

/**
 * Model class for representing a project
 */
@Entity
data class Project(
        var description: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var subProjectIds: MutableList<Int> = mutableListOf()
    var picNoteIds: MutableList<Int> = mutableListOf()
    @Ignore
    var subProjects: MutableList<Project> = mutableListOf()
    @Ignore
    var picNotes: MutableList<PicNote> = mutableListOf()
}
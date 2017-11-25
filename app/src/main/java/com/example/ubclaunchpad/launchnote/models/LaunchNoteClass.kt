package com.example.ubclaunchpad.launchnote.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Model class for representing a school class
 */
@Entity
data class LaunchNoteClass(
        var description: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var subProjectIds: MutableList<Int> = mutableListOf()
    var picNoteIds: MutableList<Int> = mutableListOf()
}
package com.example.ubclaunchpad.launchnote.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Model class for representing a school class
 */
@Entity
data class Class(
        var description: String = "",
        var subProjectIds: MutableList<Int> = mutableListOf(),
        var picNoteIds: MutableList<Int> = mutableListOf()
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
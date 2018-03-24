package com.example.ubclaunchpad.launchnote.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Model class for representing a Folder
 */
@Entity
data class Folder(
        var description: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var name: String = ""

    var parentFolderId: Int = DEFAULT_FOLDERID
    var picNoteIds: MutableList<Int> = mutableListOf()

    companion object {
        const val DEFAULT_FOLDERID = 0
    }
}
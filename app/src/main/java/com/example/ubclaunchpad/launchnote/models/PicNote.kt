package com.example.ubclaunchpad.launchnote.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.graphics.Bitmap

/**
 * Model class for representing one photo
 */
@Entity
data class PicNote(
        var imageUri: String = "",
        var description: String = "",
        var classId: Int = 0,
        var projectId: Int = 0,
        @Ignore
        var image: Bitmap? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
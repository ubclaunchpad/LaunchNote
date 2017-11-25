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
        var title: String = "",
        var description: String = "",
        @Ignore
        var image: Bitmap? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
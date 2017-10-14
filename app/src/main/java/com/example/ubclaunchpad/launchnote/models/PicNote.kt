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
        @PrimaryKey
        var id: String = "",
        var imageUri: String = "",
        var description: String = "",
        @Ignore
        var image: Bitmap? = null
)
package com.example.sherryuan.launchnote.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.graphics.Bitmap

/**
 * Model class for representing one photo
 */
@Entity
class PicNote (
    @PrimaryKey
    val id: String,
    val imageUri: String,
    var description: String,
    @Ignore
    var image: Bitmap? = null
)

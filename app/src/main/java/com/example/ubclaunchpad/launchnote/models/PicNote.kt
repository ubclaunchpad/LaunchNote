package com.example.ubclaunchpad.launchnote.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.graphics.Bitmap
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase
import java.io.File
import java.io.IOException

/**
 * Model class for representing one photo
 */
@Entity
data class PicNote(
        var imageUri: String = "",
        var compressedImageUri: String = "",
        var description: String = "",
        @Ignore
        var image: Bitmap? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var classId: Int = 0
    var projectId: Int = 0

    companion object {
        fun deleteFromDb(context: Context, pn: PicNote): Boolean {
            var success = true
            try {
                File(pn.compressedImageUri).delete()
            } catch (e: IOException) {
                e.printStackTrace()
                success = false
            }
            try {
                File(pn.imageUri).delete()
            } catch (e: IOException) {
                e.printStackTrace()
                success = false
            }
            LaunchNoteDatabase.getDatabase(context)?.picNoteDao()?.delete(pn)
            return success
        }
    }
}
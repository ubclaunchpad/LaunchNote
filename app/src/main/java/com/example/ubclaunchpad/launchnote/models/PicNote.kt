package com.example.ubclaunchpad.launchnote.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.graphics.Bitmap
import com.example.ubclaunchpad.launchnote.database.LaunchNoteDatabase
import java.io.File
import java.io.IOException
import java.io.Serializable

/**
 * Model class for representing one photo
 */
@Entity
data class PicNote(
        var imageUri: String = "",
        var compressedImageUri: String = "",
        var title: String = "",
        var description: String = "",

        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        @Ignore
        var image: Bitmap? = null
) : Serializable {
    var classId: Int = DEFAULT_CLASSID
    var projectId: Int = DEFAULT_PROJECTID

    companion object {
        /**
         * Deletes a PicNote from the database and it also deletes any photo that the database is
         * referencing
         */
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

        const val DEFAULT_CLASSID = 0
        const val DEFAULT_PROJECTID = 0
    }
}
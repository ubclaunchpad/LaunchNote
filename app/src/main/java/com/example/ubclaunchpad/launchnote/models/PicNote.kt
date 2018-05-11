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
import java.util.*

/**
 * Model class for representing one photo
 */
@Entity
data class PicNote(
        @PrimaryKey
        var id: Int = 0,
        var imageUri: String = "",
        var compressedImageUri: String = "",
        var title: String = "",
        var description: String = "",
        @Ignore
        var image: Bitmap? = null
) : Serializable {
    var folderId = Folder.DEFAULT_FOLDERID

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
            // remove PicNote from folder
            LaunchNoteDatabase.getDatabase(context)?.let { db ->
                db.folderDao().loadAll().firstElement()
                        .subscribe {
                            it.forEach { folder ->
                                if (folder.picNoteIds.contains(pn.id)) {
                                    folder.picNoteIds.remove(pn.id)
                                    if (folder.picNoteIds.isEmpty()) {
                                        db.folderDao().delete(folder)
                                    } else {
                                        db.folderDao().insert(folder)
                                    }
                                }
                            }
                        }
            }
            return success
        }

        fun generateID(): Int = Random().nextInt()
    }
}
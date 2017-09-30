package com.example.sherryuan.launchnote.database

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.example.sherryuan.launchnote.models.PicNote

/**
 * Database for PicNotes
 */
@Database(entities = arrayOf(PicNote::class), version = 1)
abstract class PicNoteDatabase : RoomDatabase() {
    abstract fun picNoteDao(): PicNoteDao
}
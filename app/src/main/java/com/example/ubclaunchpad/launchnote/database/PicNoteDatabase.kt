package com.example.ubclaunchpad.launchnote.database

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import com.example.ubclaunchpad.launchnote.database.PicNoteDao
import com.example.ubclaunchpad.launchnote.models.PicNote

/**
 * Database for PicNote
 */
@Database(entities = arrayOf(PicNote::class), version = 1)
abstract class PicNoteDatabase : RoomDatabase() {
    abstract fun picNoteDao(): PicNoteDao
}
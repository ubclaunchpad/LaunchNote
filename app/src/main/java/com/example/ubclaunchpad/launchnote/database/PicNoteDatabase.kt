package com.example.ubclaunchpad.launchnote.database

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.content.Context
import com.example.ubclaunchpad.launchnote.models.PicNote

/**
 * Database for PicNote
 */
@Database(entities = arrayOf(PicNote::class), version = 2)
abstract class PicNoteDatabase : RoomDatabase() {

    abstract fun picNoteDao(): PicNoteDao

    // in Kotlin, there is no static keyword.
    // If you want something to be an instance of a class, put it in the companion object block
    companion object {
        private var INSTANCE: PicNoteDatabase? = null
        const val DB_NAME = "PICNOTE_DB"

        // database is a singleton
        fun getDatabase(context: Context): PicNoteDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder<PicNoteDatabase>(context.applicationContext, PicNoteDatabase::class.java, DB_NAME)
                        .build()
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
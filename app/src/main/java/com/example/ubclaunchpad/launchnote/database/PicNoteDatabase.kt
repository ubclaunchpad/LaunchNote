package com.example.ubclaunchpad.launchnote.database

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.content.Context
import com.example.ubclaunchpad.launchnote.models.PicNote

/**
 * Database for PicNote
 */
@Database(entities = arrayOf(PicNote::class), version = 1)
abstract class PicNoteDatabase : RoomDatabase() {

    abstract fun picNoteDao(): PicNoteDao

    companion object {
        private var INSTANCE: PicNoteDatabase? = null
        const val DB_NAME = "PICNOTE_DB"

        fun getDatabase(context: Context): PicNoteDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder<PicNoteDatabase>(context.applicationContext, PicNoteDatabase::class.java, DB_NAME)
                        // To simplify the codelab, allow queries on the main thread.
                        // Don't do this on a real app! See PersistenceBasicSample for an example.
                        .allowMainThreadQueries()
                        .build()
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
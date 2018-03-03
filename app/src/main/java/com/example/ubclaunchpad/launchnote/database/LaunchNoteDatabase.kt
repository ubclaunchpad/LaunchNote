package com.example.ubclaunchpad.launchnote.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.example.ubclaunchpad.launchnote.models.PicNote
import com.example.ubclaunchpad.launchnote.models.Folder


/**
 * Database for PicNote
 */
@Database(entities = arrayOf(PicNote::class, Folder::class), version = 1)
@TypeConverters(Converters::class)
abstract class LaunchNoteDatabase : RoomDatabase() {

    abstract fun picNoteDao(): PicNoteDao

    abstract fun projectDao(): FolderDao

    // in Kotlin, there is no static keyword.
    // If you want something to be an instance of a class, put it in the companion object block
    companion object {
        private var INSTANCE: LaunchNoteDatabase? = null
        const val DB_NAME = "PICNOTE_DB"


        // database is a singleton
        fun getDatabase(context: Context): LaunchNoteDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder<LaunchNoteDatabase>(context.applicationContext, LaunchNoteDatabase::class.java, DB_NAME)
                        .build()
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
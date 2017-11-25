package com.example.ubclaunchpad.launchnote.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import com.example.ubclaunchpad.launchnote.database.Converters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Model class for representing a school class
 */
@Entity
@TypeConverters(Converters::class)
data class LaunchNoteClass(
        var description: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var subProjectIds: MutableList<Int> = mutableListOf()
    var picNoteIds: MutableList<Int> = mutableListOf()

//    companion object {
//
//        // convert from String to list of Ints
//        @TypeConverter
//        fun fromString(value: String): MutableList<Int> =
//                Gson().fromJson(value, object : TypeToken<MutableList<Int>>() {}.type)
//
//        // convert from list of Ints to String
//        @TypeConverter
//        fun fromIntList(list: MutableList<Int>): String =
//                Gson().toJson(list)
//    }
}
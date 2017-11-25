package com.example.ubclaunchpad.launchnote.database

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Helper class for converting between Java objects and things that can be stored in SQL
 */
class Converters {
    // convert from String to list of Ints
    @TypeConverter
    fun fromString(value: String): MutableList<Int> =
            Gson().fromJson(value, object : TypeToken<MutableList<Int>>() {}.type)

    // convert from list of Ints to String
    @TypeConverter
    fun fromIntList(list: MutableList<Int>): String =
            Gson().toJson(list)
}
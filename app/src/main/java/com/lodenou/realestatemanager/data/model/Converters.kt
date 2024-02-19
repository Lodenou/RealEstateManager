package com.lodenou.realestatemanager.data.model

import androidx.room.TypeConverter
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class Converters {

    // DATE CONVERSION FOR REAL-ESTATE OBJECT
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? = value?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    @TypeConverter
    fun localDateToTimestamp(date: LocalDate?): Long? =
        date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()


    // List image CONVERSION FOR REAL-ESTATE OBJECT
    @TypeConverter
    fun fromImageList(value: List<ImageWithDescription>?): String = Gson().toJson(value)

    @TypeConverter
    fun toImageList(value: String): List<ImageWithDescription>? = Gson().fromJson(value, object :
        TypeToken<List<ImageWithDescription>>() {}.type)

    //List string CONVERSION FOR REAL-ESTATE OBJECT
    @TypeConverter
    fun fromStringList(strings: List<String>?): String {
        return Gson().toJson(strings)
    }

    @TypeConverter
    fun toStringList(string: String): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(string, listType)
    }

}
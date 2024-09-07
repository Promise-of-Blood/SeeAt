package com.pob.seeat.data.database

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.util.Date

object TimestampConverter {
    @TypeConverter
    fun dateToTimestamp(value: Date?): Timestamp? {
        return value?.let { Timestamp(it) }
    }

    @TypeConverter
    fun timestampToDate(value: Timestamp?): Date? {
        return value?.toDate()
    }

    @TypeConverter
    fun longToDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToLong(value: Date?): Long? {
        return value?.time
    }
}

object ListConverter {
    @TypeConverter
    fun stringToList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun listToString(list: List<String>?): String? {
        return list?.joinToString(",")
    }
}
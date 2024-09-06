package com.pob.seeat.data.database

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateTimeConverter {
    @TypeConverter
    fun stringToLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }

    @TypeConverter
    fun localDateTimeToString(date: LocalDateTime?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
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
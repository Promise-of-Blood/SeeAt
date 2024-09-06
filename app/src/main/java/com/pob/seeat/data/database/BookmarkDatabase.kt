package com.pob.seeat.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pob.seeat.data.model.BookmarkEntity

private const val DATABASE_NAME = "bookmark_database"

@Database(entities = [BookmarkEntity::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class, ListConverter::class)
abstract class BookmarkDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        fun create(context: Context): BookmarkDatabase {
            return Room.databaseBuilder(
                context,
                BookmarkDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}
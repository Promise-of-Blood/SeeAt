package com.pob.seeat.data.database.chat

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ChatEntity::class], version = 1)
abstract class ChatRoomDb : RoomDatabase() {
    abstract fun chatDao(): ChatDao

    companion object {
        private var INSTANCE : ChatRoomDb ?= null

        fun getDatabase(context: Context): ChatRoomDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatRoomDb::class.java,
                    "chatDatabase"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
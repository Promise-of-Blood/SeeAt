package com.pob.seeat.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pob.seeat.data.model.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmark_table")
    fun getBookmarkList(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insetBookmarkList(bookmarkList: List<BookmarkEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmark(feed: BookmarkEntity)

    @Query("DELETE FROM bookmark_table WHERE feedId = :feedId")
    suspend fun deleteBookmark(feedId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmark_table WHERE feedId = :feedId)")
    suspend fun isBookmarkExists(feedId: String): Boolean
}
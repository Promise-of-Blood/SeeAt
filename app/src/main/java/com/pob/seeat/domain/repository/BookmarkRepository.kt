package com.pob.seeat.domain.repository

import com.pob.seeat.data.model.BookmarkEntity
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.BookmarkModel
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    suspend fun getBookmarkList(): Flow<Result<List<BookmarkModel>>>
    suspend fun saveBookmarkList(list: List<BookmarkModel>)
    suspend fun saveBookmark(feed: BookmarkEntity)
    suspend fun deleteBookmark(feedId: String)
    suspend fun deleteBookmark(feedIdList: List<String>)
    suspend fun isBookmarked(feedId: String): Boolean
}
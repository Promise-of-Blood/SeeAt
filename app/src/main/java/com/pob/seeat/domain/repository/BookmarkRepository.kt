package com.pob.seeat.domain.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.BookmarkModel
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    suspend fun getBookmarkList(): Flow<Result<List<BookmarkModel>>>
    suspend fun saveBookmark(feed: BookmarkModel)
    suspend fun deleteBookmark(feedId: String)
}
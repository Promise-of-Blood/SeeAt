package com.pob.seeat.data.repository

import com.pob.seeat.data.database.BookmarkDao
import com.pob.seeat.data.model.BookmarkEntity
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.BookmarkModel
import com.pob.seeat.domain.model.toBookmarkEntity
import com.pob.seeat.domain.model.toBookmarkList
import com.pob.seeat.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {
    override suspend fun getBookmarkList(): Flow<Result<List<BookmarkModel>>> = flow {
        emit(Result.Loading)
        try {
            bookmarkDao.getBookmarkList().collect {
                emit(Result.Success(it.toBookmarkList()))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun saveBookmarkList(list: List<BookmarkModel>) {
        bookmarkDao.insetBookmarkList(list.map(BookmarkModel::toBookmarkEntity))
    }

    override suspend fun saveBookmark(feed: BookmarkEntity) {
        bookmarkDao.insertBookmark(feed)
    }

    override suspend fun deleteBookmark(feedId: String) {
        bookmarkDao.deleteBookmark(feedId)
    }

    override suspend fun isBookmarked(feedId: String): Boolean {
        return bookmarkDao.isBookmarkExists(feedId)
    }
}
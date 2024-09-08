package com.pob.seeat.domain.usecase

import com.pob.seeat.data.model.BookmarkEntity
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.BookmarkModel
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.repository.BookmarkRepository
import com.pob.seeat.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class GetBookmarkListUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke() = bookmarkRepository.getBookmarkList()
}

class SaveBookmarkListUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(list: List<BookmarkModel>) =
        bookmarkRepository.saveBookmarkList(list)
}

class SaveBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(feed: BookmarkEntity) = bookmarkRepository.saveBookmark(feed)
}

class DeleteBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(feedId: String) = bookmarkRepository.deleteBookmark(feedId)
}

class FetchBookmarkListUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val feedRepository: FeedRepository,
) {
    suspend operator fun invoke(feedIdList: List<String>): Flow<Result<List<FeedModel>>> {
        val bookmarkList = feedRepository.getFeedListById(feedIdList)
        val newIdList = mutableListOf<String>()
        bookmarkList.map { list ->
            if (list is Result.Success) newIdList.addAll(list.data.map { it.feedId })
        }
        bookmarkRepository.deleteBookmark(newIdList)
        return bookmarkList
    }
}

class IsBookmarkedUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(feedId: String) = bookmarkRepository.isBookmarked(feedId)
}
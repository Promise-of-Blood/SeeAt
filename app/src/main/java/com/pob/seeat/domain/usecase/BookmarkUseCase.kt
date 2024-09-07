package com.pob.seeat.domain.usecase

import com.pob.seeat.data.model.BookmarkEntity
import com.pob.seeat.domain.model.BookmarkModel
import com.pob.seeat.domain.repository.BookmarkRepository
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

class IsBookmarkedUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(feedId: String) = bookmarkRepository.isBookmarked(feedId)
}
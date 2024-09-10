package com.pob.seeat.domain.usecase

import com.pob.seeat.domain.repository.FeedRepository
import javax.inject.Inject

class RemoveFeedUseCase @Inject constructor(private val feedRepository: FeedRepository) {
    suspend operator fun invoke(
        feedId: String
    ){
        return feedRepository.removeFeed(feedId)
    }
}
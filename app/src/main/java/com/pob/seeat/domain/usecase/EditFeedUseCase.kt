package com.pob.seeat.domain.usecase

import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.repository.FeedRepository
import timber.log.Timber
import javax.inject.Inject

class EditFeedUseCase @Inject constructor(private val feedRepository: FeedRepository) {
    suspend operator fun invoke(
        feedModel: FeedModel
    ) {
        Timber.tag("editFeedRemote").i("editFeedUseCase: $feedModel")
        return feedRepository.editFeed(feedModel)
    }
}
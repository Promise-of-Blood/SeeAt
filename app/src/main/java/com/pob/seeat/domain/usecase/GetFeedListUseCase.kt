package com.pob.seeat.domain.usecase

import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.repository.FeedListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFeedListUseCase @Inject constructor(private val feedListRepository: FeedListRepository) {

    suspend fun execute(
        query: String
    ): Flow<Result<List<FeedModel>>> {
        return feedListRepository.getFeedList(query)
    }

    suspend operator fun invoke(
        query: String
    ): Flow<Result<List<FeedModel>>> {
        return feedListRepository.getFeedList(query)
    }
}
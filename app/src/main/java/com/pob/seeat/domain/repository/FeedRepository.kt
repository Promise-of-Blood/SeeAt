package com.pob.seeat.domain.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.FeedModel
import kotlinx.coroutines.flow.Flow

interface FeedListRepository {
    suspend fun getFeedList(query: String): Flow<Result<List<FeedModel>>>
}
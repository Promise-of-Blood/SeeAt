package com.pob.seeat.domain.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.FeedModel
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun getFeedList(): Flow<Result<List<FeedModel>>>
    suspend fun getFeedList(uid: String): Flow<Result<List<FeedModel>>>
    suspend fun getFeed(feedId: String): Flow<Result<FeedModel>>
}
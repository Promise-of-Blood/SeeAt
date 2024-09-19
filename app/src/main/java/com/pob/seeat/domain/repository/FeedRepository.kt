package com.pob.seeat.domain.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.FeedModel
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun getFeedList(): Flow<Result<List<FeedModel>>>
    suspend fun getFeed(feedId: String): Flow<Result<FeedModel>>
    suspend fun getFeedListById(feedIdList: List<String>): Flow<Result<List<FeedModel>>>
    suspend fun setLikePlus(feedId: String)
    suspend fun setLikeMinus(feedId: String)
    suspend fun removeFeed(feedId: String)
    suspend fun editFeed(feedModel: FeedModel)
}
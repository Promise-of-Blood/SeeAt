package com.pob.seeat.domain.repository

import com.google.firebase.firestore.GeoPoint
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.FeedModel
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun getFeedList(
        centerLat: Double,
        centerLng: Double,
        userLocation: GeoPoint,
        radiusInKm: Double,
        sortBy: String,
    ): Flow<Result<List<FeedModel>>>

    suspend fun getFeed(feedId: String, userLocation: GeoPoint): Flow<Result<FeedModel>>
    suspend fun getFeedListById(feedIdList: List<String>): Flow<Result<List<FeedModel>>>
    suspend fun setLikePlus(feedId: String)
    suspend fun setLikeMinus(feedId: String)
    suspend fun removeFeed(feedId: String)
    suspend fun editFeed(feedModel: FeedModel)
}
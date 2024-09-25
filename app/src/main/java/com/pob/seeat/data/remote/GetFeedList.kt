package com.pob.seeat.data.remote

import com.pob.seeat.domain.model.FeedModel

interface GetFeedList {
    suspend fun getFeedList(
        centerLat: Double,
        centerLng: Double,
        radiusInKm: Double
    ): List<FeedModel>
}
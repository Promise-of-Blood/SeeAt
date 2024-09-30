package com.pob.seeat.data.remote

import com.google.firebase.firestore.GeoPoint
import com.pob.seeat.domain.model.FeedModel

interface GetFeedList {
    suspend fun getFeedList(
        centerLat: Double,
        centerLng: Double,
        userLocation: GeoPoint,
        radiusInKm: Double
    ): List<FeedModel>
}
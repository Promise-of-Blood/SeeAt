package com.pob.seeat.data.remote

import com.google.firebase.firestore.GeoPoint
import com.pob.seeat.domain.model.FeedModel

interface DetailFeed {
    suspend fun getFeedById(postId: String, userLocation: GeoPoint): FeedModel?
    suspend fun updateLikePlus(postId: String)
    suspend fun updateLikeMinus(postId: String)
    suspend fun removeFeed(postId: String)
    suspend fun editFeed(feedModel: FeedModel)
}
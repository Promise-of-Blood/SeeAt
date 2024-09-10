package com.pob.seeat.data.remote

import com.pob.seeat.domain.model.FeedModel

interface DetailFeed {
    suspend fun getFeedById(postId: String): FeedModel?
    suspend fun updateLikePlus(postId: String)
    suspend fun updateLikeMinus(postId: String)
    suspend fun removeFeed(postId: String)
}
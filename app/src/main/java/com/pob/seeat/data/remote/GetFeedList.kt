package com.pob.seeat.data.remote

import com.pob.seeat.domain.model.FeedModel

interface GetFeedList {
    suspend fun getFeedList(
        uid: String? = null,
        limit: Long? = null,
        startAfter: String? = null
    ): List<FeedModel>
}
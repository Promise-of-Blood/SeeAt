package com.pob.seeat.data.remote

import com.pob.seeat.domain.model.FeedModel

interface GetFeedList {
    suspend fun getFeedList(uid: String? = null): List<FeedModel>
}
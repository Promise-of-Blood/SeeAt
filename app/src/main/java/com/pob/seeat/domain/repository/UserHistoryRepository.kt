package com.pob.seeat.domain.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.CommentHistoryModel
import com.pob.seeat.domain.model.FeedModel
import kotlinx.coroutines.flow.Flow

interface UserHistoryRepository {
    suspend fun getFeedList(
        uid: String,
        limit: Long? = null,
        startAfter: String? = null
    ): Flow<Result<List<FeedModel>>>

    suspend fun getCommentList(
        uid: String,
        limit: Long? = null,
        startAfter: String? = null
    ): Flow<Result<List<CommentHistoryModel>>>

    suspend fun getLikedList(
        uid: String,
        limit: Long? = null,
        startAfter: String? = null
    ): Flow<Result<List<FeedModel>>>
}
package com.pob.seeat.domain.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.domain.model.FeedModel
import kotlinx.coroutines.flow.Flow

interface UserHistoryRepository {
    suspend fun getFeedList(uid: String): Flow<Result<List<FeedModel>>>
    suspend fun getCommentList(uid: String): Flow<Result<List<CommentModel>>>
    suspend fun getLikedList(uid: String): Flow<Result<List<FeedModel>>>
}
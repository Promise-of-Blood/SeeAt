package com.pob.seeat.data.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.data.remote.UserHistoryRemote
import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.repository.UserHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserHistoryRepositoryImpl @Inject constructor(
    private val userHistoryRemote: UserHistoryRemote
) : UserHistoryRepository {
    override suspend fun getFeedList(uid: String): Flow<Result<List<FeedModel>>> = flow {
        emit(Result.Loading)
        if (uid.isEmpty()) emit(Result.Error("Invalid user ID"))
        try {
            val data = userHistoryRemote.getFeedList(uid)
            emit(Result.Success(data))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun getCommentList(uid: String): Flow<Result<List<FeedModel>>> = flow {
        emit(Result.Loading)
        if (uid.isEmpty()) emit(Result.Error("Invalid user ID"))
        try {
            val data = userHistoryRemote.getCommentList(uid)
            emit(Result.Success(data))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun getLikedList(uid: String): Flow<Result<List<FeedModel>>> = flow {
        // TODO 좋아요 한 글 가져오기
        emit(Result.Loading)
        if (uid.isEmpty()) emit(Result.Error("Invalid user ID"))
        try {
            val data = userHistoryRemote.getFeedList(uid)
            emit(Result.Success(data))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }
}
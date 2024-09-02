package com.pob.seeat.data.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.data.remote.UserHistoryRemote
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.repository.UserHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserHistoryRepositoryImpl @Inject constructor(
    private val userHistoryRemote: UserHistoryRemote
) : UserHistoryRepository {
    override suspend fun getFeedList(
        uid: String,
        limit: Long?,
        startAfter: String?
    ): Flow<Result<List<FeedModel>>> = flow {
        emit(Result.Loading)
        if (uid.isEmpty()) emit(Result.Error("Invalid user ID"))
        try {
            val data = userHistoryRemote.getFeedList(uid, limit, startAfter)
            emit(Result.Success(data))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun getCommentList(
        uid: String,
        limit: Long?,
        startAfter: String?
    ): Flow<Result<List<FeedModel>>> = flow {
        emit(Result.Loading)
        if (uid.isEmpty()) emit(Result.Error("Invalid user ID"))
        try {
            val data = userHistoryRemote.getCommentList(uid, limit, startAfter)
            emit(Result.Success(data))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun getLikedList(
        uid: String,
        limit: Long?,
        startAfter: String?
    ): Flow<Result<List<FeedModel>>> = flow {
        emit(Result.Loading)
        if (uid.isEmpty()) emit(Result.Error("Invalid user ID"))
        try {
            val data = userHistoryRemote.getLikedList(uid, limit, startAfter)
            emit(Result.Success(data))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }
}
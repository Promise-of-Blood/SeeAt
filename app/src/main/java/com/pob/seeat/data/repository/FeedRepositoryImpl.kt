package com.pob.seeat.data.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.data.remote.FeedRemote
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val feedRemote: FeedRemote
): FeedRepository {

    override suspend fun getFeedList(): Flow<Result<List<FeedModel>>> = flow {
        emit(Result.Loading)
        try {
            val posts = feedRemote.getFeedList()
            emit(Result.Success(posts))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun getFeedList(uid: String): Flow<Result<List<FeedModel>>> = flow {
        emit(Result.Loading)
        if (uid.isEmpty()) emit(Result.Error("Invalid user ID"))
        try {
            val posts = feedRemote.getFeedList(uid)
            emit(Result.Success(posts))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun getFeed(feedId: String): Flow<Result<FeedModel>> = flow {
        emit(Result.Loading)
        try {
            val feed = feedRemote.getFeedById(feedId)
            feed?.let {
                emit(Result.Success(it))
            } ?: emit(Result.Error("Post not found"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }
}
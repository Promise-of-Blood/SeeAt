package com.pob.seeat.data.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.data.remote.FeedRemote
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    private val feedRemote: FeedRemote
) : FeedRepository {

    override suspend fun getFeedList(centerLat: Double, centerLng: Double, radiusInKm: Double): Flow<Result<List<FeedModel>>> = flow {
        emit(Result.Loading)
        try {
            val posts = feedRemote.getFeedList(centerLat, centerLng, radiusInKm)
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

    override suspend fun getFeedListById(feedIdList: List<String>): Flow<Result<List<FeedModel>>> =
        flow {
            emit(Result.Loading)
            try {
                val feedList = feedRemote.getFeedListById(feedIdList)
                emit(Result.Success(feedList))
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "An unknown error occurred"))
            }
        }

    override suspend fun setLikePlus(feedId: String) {
        try {
            feedRemote.updateLikePlus(feedId)
        } catch (e: Exception) {
            Timber.i(e.toString())
        }
    }

    override suspend fun setLikeMinus(feedId: String) {
        try {
            feedRemote.updateLikeMinus(feedId)
        } catch (e: Exception) {
            Timber.i(e.toString())
        }
    }

    override suspend fun removeFeed(feedId: String) {
        try {
            feedRemote.removeFeed(feedId)
        } catch (e: Exception) {
            Timber.i(e.toString())
        }
    }
}
package com.pob.seeat.data.repository

import com.pob.seeat.data.model.Result
import com.pob.seeat.data.remote.FeedRemoteDataSource
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.model.toFeedModelList
import com.pob.seeat.domain.repository.FeedListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(private val feedRemoteDataSource: FeedRemoteDataSource) :
    FeedListRepository {
    override suspend fun getFeedList(query: String): Flow<Result<List<FeedModel>>> {
        return flow {
            val feedList = feedRemoteDataSource.getFeedList(query)
            val feedResponse = if (feedList.isNotEmpty()) Result.Success(feedList.toFeedModelList())
            else Result.Error("피드 목록을 가져오지 못했습니다.")
            emit(feedResponse)
        }
    }


}
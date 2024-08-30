package com.pob.seeat.domain.usecase

import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.repository.FeedRepository
import com.pob.seeat.domain.repository.UserInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UserHistoryUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val feedRepository: FeedRepository,
) {
    // 현재 로그인 한 사용자가 작성한 글
    suspend fun execute(): Flow<Result<List<FeedModel>>> {
        val currentUserId = userInfoRepository.getCurrentUserUid().firstOrNull() ?: ""
        return feedRepository.getFeedList(currentUserId)
    }
}
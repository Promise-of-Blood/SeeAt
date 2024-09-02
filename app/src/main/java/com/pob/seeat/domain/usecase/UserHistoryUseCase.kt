package com.pob.seeat.domain.usecase

import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.repository.UserHistoryRepository
import com.pob.seeat.domain.repository.UserInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

data class UserHistoryUseCases(
    val userFeedHistoryUseCase: UserFeedHistoryUseCase,
    val userCommentHistoryUseCase: UserCommentHistoryUseCase,
    val userLikedHistoryUseCase: UserLikedHistoryUseCase,
)

class UserFeedHistoryUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val userHistoryRepository: UserHistoryRepository,
) {
    // 현재 로그인 한 사용자가 작성한 글
    suspend operator fun invoke(
        limit: Long? = null,
        startAfter: String? = null
    ): Flow<Result<List<FeedModel>>> {
        val currentUserId = userInfoRepository.getCurrentUserUid().firstOrNull() ?: ""
        return userHistoryRepository.getFeedList(currentUserId, limit, startAfter)
    }
}

class UserCommentHistoryUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val userHistoryRepository: UserHistoryRepository,
) {
    suspend operator fun invoke(
        limit: Long? = null,
        startAfter: String? = null
    ): Flow<Result<List<FeedModel>>> {
        val currentUserId = userInfoRepository.getCurrentUserUid().firstOrNull() ?: ""
        return userHistoryRepository.getCommentList(currentUserId, limit, startAfter)
    }
}

class UserLikedHistoryUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val userHistoryRepository: UserHistoryRepository,
) {
    // TODO 좋아요 한 글 가져오기
    suspend operator fun invoke(
        limit: Long? = null,
        startAfter: String? = null
    ): Flow<Result<List<FeedModel>>> {
        val currentUserId = userInfoRepository.getCurrentUserUid().firstOrNull() ?: ""
        return userHistoryRepository.getFeedList(currentUserId, limit, startAfter)
    }
}
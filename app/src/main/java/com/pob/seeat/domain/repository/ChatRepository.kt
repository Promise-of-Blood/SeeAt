package com.pob.seeat.domain.repository

import kotlinx.coroutines.flow.Flow
import com.pob.seeat.data.model.Result
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem

interface ChatRepository {
    suspend fun sendMessage(feedId: String, targetUid: String, message: String)
    suspend fun receiveMessage(feedId: String): Flow<Result<ChattingUiItem>>
    suspend fun initMessage(feedId: String): List<Result<ChattingUiItem>>
}
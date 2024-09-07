package com.pob.seeat.domain.repository

import com.pob.seeat.domain.model.CommentModel
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    suspend fun createComment(commentModel: CommentModel)
    suspend fun getCommentList(feedId: String) : Flow<List<CommentModel>>
    suspend fun deleteComment(commentData: CommentModel)
    suspend fun editComment(commentData: CommentModel)
    suspend fun getComment(feedId: String,commentId:String) : CommentModel?
}
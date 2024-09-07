package com.pob.seeat.data.remote

import com.pob.seeat.data.model.CommentData
import kotlinx.coroutines.flow.Flow

interface CommentSource {
    suspend fun createComment(commentData: CommentData)
    suspend fun getCommentList(feedId: String) : Flow<List<CommentData>>
    suspend fun deleteComment(commentData: CommentData)
    suspend fun editComment(commentData: CommentData)
    suspend fun getComment(feedId: String, commentId:String) : CommentData?
}
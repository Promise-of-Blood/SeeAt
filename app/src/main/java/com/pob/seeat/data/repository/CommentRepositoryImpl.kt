package com.pob.seeat.data.repository

import com.pob.seeat.data.model.toCommentModel
import com.pob.seeat.data.remote.CommentSource
import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.domain.model.toCommentData
import com.pob.seeat.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(private val source : CommentSource):CommentRepository {
    override suspend fun createComment(commentModel: CommentModel) {
        source.createComment(commentModel.toCommentData())
    }

    override suspend fun getCommentList(feedId: String): Flow<List<CommentModel>> {
        return source.getCommentList(feedId).map { commentDataList ->
            commentDataList.map { it.toCommentModel() }
        }
    }

    override suspend fun deleteComment(commentModel: CommentModel) {
        source.deleteComment(commentModel.toCommentData())
    }

    override suspend fun editComment(commentModel: CommentModel) {
        source.editComment(commentModel.toCommentData())
    }

    override suspend fun getComment(feedId: String, commentId: String): CommentModel? {
        val commentData = source.getComment(feedId,commentId)
        return commentData?.toCommentModel()
    }
}
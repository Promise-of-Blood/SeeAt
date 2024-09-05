package com.pob.seeat.domain.usecase

import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

data class CommentUseCases(
    val createCommentUseCases: CreateCommentUseCases,
    val getCommentListUsesCases: GetCommentListUseCases,
    val deleteCommentUseCases: DeleteCommentUseCases,
    val editCommentUseCases: EditCommentUseCases
)

class CreateCommentUseCases(private val repository: CommentRepository){
    suspend fun execute(commentModel : CommentModel){
        repository.createComment(commentModel)
    }
}

class GetCommentListUseCases(private val repository: CommentRepository){
    suspend fun execute(feedId:String): Flow<List<CommentModel>> {
        return repository.getCommentList(feedId)
    }
}

class DeleteCommentUseCases(private val repository: CommentRepository){
    suspend fun execute(commentModel : CommentModel){
        repository.deleteComment(commentModel)
    }
}

class EditCommentUseCases(private val repository: CommentRepository){
    suspend fun execute(commentModel : CommentModel){
        repository.editComment(commentModel)
    }
}
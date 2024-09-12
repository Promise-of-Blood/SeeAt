package com.pob.seeat.domain.usecase

import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow

data class CommentUseCases(
    val createCommentUseCases: CreateCommentUseCases,
    val getCommentListUsesCases: GetCommentListUseCases,
    val deleteCommentUseCases: DeleteCommentUseCases,
    val updateCommentUseCases: UpdateCommentUseCases,
    val getCommentUseCases: GetCommentUseCases
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

class UpdateCommentUseCases(private val repository: CommentRepository){
    suspend fun execute(commentModel : CommentModel){
        repository.updateComment(commentModel)
    }
}

class GetCommentUseCases(private val repository: CommentRepository){
    suspend fun execute(feedId: String, commentId:String):CommentModel?{
        return repository.getComment(feedId,commentId)
    }
}
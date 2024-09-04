package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.domain.usecase.CommentUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.parcelize.RawValue
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(private val commentUseCases: CommentUseCases) :
    ViewModel() {
    private val _comments = MutableStateFlow<List<CommentModel>>(emptyList())
    val comments: StateFlow<List<CommentModel>> get() = _comments


    fun addComment(
        feedId: String = "",
        commentId: String = "",
        user: @RawValue DocumentReference? = null,
        comment: String = "",
        likeCount: Int = 0,
        timeStamp: Timestamp? = null,
        userImage: String = "",
        userNickname: String = ""
    ) {

        val newComment = CommentModel(feedId,commentId,user,comment,likeCount,timeStamp,userImage,userNickname)

        viewModelScope.launch {
            createComment(newComment)
        }
    }

    fun createComment(commentModel: CommentModel) {
        viewModelScope.launch {
            commentUseCases.createCommentUseCases.execute(commentModel)
        }
    }

    fun fetchComments(feedId: String) {
        viewModelScope.launch {
            commentUseCases.getCommentListUsesCases.execute(feedId).collect{comments ->
                _comments.value = comments
            }
        }


    }

    fun deleteComment(commentModel: CommentModel) {
        viewModelScope.launch {
            commentUseCases.deleteCommentUseCases.execute(commentModel)
        }
    }

    fun editComment(commentModel: CommentModel) {
        viewModelScope.launch {
            commentUseCases.editCommentUseCases.execute(commentModel)
        }
    }

}
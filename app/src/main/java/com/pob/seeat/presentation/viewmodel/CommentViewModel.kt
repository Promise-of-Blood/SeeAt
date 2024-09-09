package com.pob.seeat.presentation.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pob.seeat.domain.model.CommentModel
import com.pob.seeat.domain.usecase.CommentUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.parcelize.RawValue
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(private val commentUseCases: CommentUseCases) :
    ViewModel() {
    private val _comments = MutableStateFlow<List<CommentModel>>(emptyList())
    val comments: StateFlow<List<CommentModel>> get() = _comments

    private val _selectedComment = MutableStateFlow<CommentModel?>(null)
    val selectedComment: StateFlow<CommentModel?> get() = _selectedComment

    private val _isMyComment = MutableStateFlow(false)
    val isMyComment: StateFlow<Boolean> get() = _isMyComment

    private val _isOwnerComment = MutableStateFlow(mutableMapOf<String, Boolean>())
    val isOwnerComment: StateFlow<Map<String, Boolean>> get() = _isOwnerComment

    private val _isOwnerCommentComplete = MutableStateFlow(false)
    val isOwnerCommentComplete: StateFlow<Boolean> get() = _isOwnerCommentComplete

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
        val newComment = CommentModel(
            feedId,
            commentId,
            user,
            comment,
            likeCount,
            timeStamp,
            userImage,
            userNickname
        )

        viewModelScope.launch {
            createComment(newComment)
            updateCommentCountPlus(feedId)
            withContext(Dispatchers.Main) {
                fetchComments(feedId)
                Log.d("CommentViewModel", "댓글 추가됨: ${_comments.value.size}")
            }
        }
    }

    suspend fun updateCommentCountPlus(postId: String) {
        FirebaseFirestore.getInstance().collection("feed")
            .document(postId)
            .update("commentsCount", FieldValue.increment(1))
            .await()
    }


    fun deleteMyComment(feedId: String, commentId: String, context: Context) {
        viewModelScope.launch {
            val selectedComment = commentUseCases.getCommentUseCases.execute(feedId, commentId)
            selectedComment?.let { deleteComment(it, context) }
        }
    }

    suspend fun updateCommentCountMinus(postId: String) {
        FirebaseFirestore.getInstance().collection("feed")
            .document(postId)
            .update("commentsCount", FieldValue.increment(-1))
            .await()
    }

    fun checkMyComment(commentUserUid: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUid = currentUser?.uid

        _isMyComment.value = (userUid == commentUserUid)
    }

    //비동기처리때문에 포기...
//    fun checkOwnerComment(commentUserId: String, feedId: String) {
//        viewModelScope.launch {
//            try {
//                val snapshot = FirebaseFirestore.getInstance()
//                    .collection("feed")
//                    .document(feedId)
//                    .get()
//                    .await()
//
//                if (snapshot.exists()) {
//                    val userRef = snapshot.get("user")
//                    if (userRef is DocumentReference) {
//                        val userSnapshot = userRef.get().await()
//                        if (userSnapshot.exists()) {
//                            val ownerUid = userSnapshot.getString("uid")
//                            Log.d("코멘트", "OwnerUID :$ownerUid")
//                            val isOwnerComment = (ownerUid == commentUserId)
//
//                            // UI 업데이트를 메인 스레드에서 수행
//                            withContext(Dispatchers.Main) {
//                                _isOwnerComment.update { currentMap ->
//                                    currentMap.toMutableMap().apply {
//                                        put(commentUserId, isOwnerComment)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                Log.d("코멘트", "아이디: $commentUserId , 주인이냐? : ${_isOwnerComment.value}")
//            } catch (e: Exception) {
//                Log.e("CommentViewModel", "Failed to fetch owner comment status: ${e.message}")
//            }
//        }
//    }

    fun createComment(commentModel: CommentModel) {
        viewModelScope.launch {
            commentUseCases.createCommentUseCases.execute(commentModel)
        }
    }

    fun fetchComments(feedId: String) {
        viewModelScope.launch {
            commentUseCases.getCommentListUsesCases.execute(feedId).collect { comments ->
                _comments.value = comments
            }
        }


    }

    fun deleteComment(commentModel: CommentModel, context: Context) {
        viewModelScope.launch {
            try {
                commentUseCases.deleteCommentUseCases.execute(commentModel)
                updateCommentCountMinus(commentModel.feedId)
                withContext(Dispatchers.Main) {
                    fetchComments(commentModel.feedId)
                    Log.d("CommentViewModel", "댓글 삭제됨: ${_comments.value.size}")
                }
            } catch (e: Exception) {
                Log.e("댓글삭제오류","댓글 삭제 중 오류 발생 : ${e.message}")
                Toast.makeText(context, "댓글 삭제에 실패했습니다 다시 한 번 시도해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun editComment(commentModel: CommentModel) {
        viewModelScope.launch {
            commentUseCases.editCommentUseCases.execute(commentModel)
        }
    }

    fun getComment(feedId: String, commentId: String) {
        viewModelScope.launch {
            val comment = commentUseCases.getCommentUseCases.execute(feedId, commentId)
            _selectedComment.value = comment
        }
    }

}
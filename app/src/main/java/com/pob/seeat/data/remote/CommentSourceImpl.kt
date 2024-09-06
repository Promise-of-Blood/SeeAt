package com.pob.seeat.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.pob.seeat.data.model.CommentData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : CommentSource {

    override suspend fun createComment(commentData: CommentData) {
        try {
            val documentReference =firestore.collection("feed")
                .document(commentData.feedId)
                .collection("comments")
                .add(commentData)
                .await() // await를 사용하여 작업 대기

            val generatedCommentId = documentReference.id

            firestore.collection("feed")
                .document(commentData.feedId)
                .collection("comments")
                .document(generatedCommentId)
                .update("commentId", generatedCommentId) // 문서의 commentId 필드를 업데이트
                .await()

            Log.d("댓글 작성", "성공")
        } catch (e: Exception) {
            Log.e("댓글 작성", "실패 (에러 : $e)")
        }
    }

    override suspend fun getCommentList(feedId: String) : Flow<List<CommentData>> {
        return flow {
            try {
                // Firestore에서 댓글 데이터를 가져오기
                val snapshot = firestore.collection("feed")
                    .document(feedId)
                    .collection("comments")
                    .orderBy("timeStamp",com.google.firebase.firestore.Query.Direction.ASCENDING)
                    .get()
                    .await() // 비동기 작업 대기

                // 쿼리 결과를 CommentData 객체 리스트로 변환
                val comments = snapshot.documents.mapNotNull { document ->
                    document.toObject(CommentData::class.java)
                }

                // 데이터를 Flow로 emit
                emit(comments)
            } catch (e: Exception) {
                // 예외 발생 시 처리 (예: 로그 출력)
                println("댓글 가져오기 실패: ${e.message}")
                emit(emptyList<CommentData>()) // 빈 리스트를 emit하거나 에러를 처리할 수 있음
            }
        }
    }

    override suspend fun deleteComment(commentData: CommentData) {
        firestore.collection("feed")
            .document(commentData.feedId)
            .collection("comments")
            .document(commentData.commentId)
            .delete()
            .await()
    }

    override suspend fun editComment(commentData: CommentData) {
        firestore.collection("feed")
            .document(commentData.feedId) // 동일하게 feedId 사용
            .collection("comments")
            .document(commentData.commentId)
            .set(commentData)
            .await()
    }

    override suspend fun getComment(feedId: String, commentId: String): CommentData? {
        return try {
            // Firestore에서 특정 댓글을 가져오기
            val document = firestore.collection("feed")
                .document(feedId)
                .collection("comments")
                .document(commentId)
                .get()
                .await()

            document.toObject<CommentData>() // 데이터 객체로 변환
        } catch (e: Exception) {
            Log.e("댓글 가져오기 실패", e.message ?: "알 수 없는 오류")
            null
        }
    }
}
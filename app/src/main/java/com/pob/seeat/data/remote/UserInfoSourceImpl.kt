package com.pob.seeat.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.pob.seeat.data.model.UserInfoData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserInfoSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : UserInfoSource {

    override suspend fun createUserInfo(userInfoData: UserInfoData) {
        firestore.collection("user").document(userInfoData.uid).set(userInfoData)
            .addOnSuccessListener {
                Log.d("회원가입", "성공")
            }
            .addOnFailureListener { e ->
                Log.e("회원가입", "실패 ( 에러 : $e )")
            }
    }

    override suspend fun getUserInfo(uid: String): Flow<UserInfoData?> {
        return flow {
            val myData = firestore.collection("user").document(uid).get().await()
            emit(myData.toObject<UserInfoData>())
        }
    }

    override suspend fun deleteUserInfo(userInfoData: UserInfoData) {
        firestore.collection("user").document(userInfoData.uid).delete().await()
    }

    override suspend fun updateUserInfo(userInfoData: UserInfoData) {
        firestore.collection("user").document(userInfoData.uid).set(userInfoData).await()
    }

    override suspend fun getCurrentUserUid(): Flow<String> {
        return flow { emit(firebaseAuth.currentUser?.uid ?: "") }
    }

    override suspend fun getUserInfoByEmail(email: String): Flow<UserInfoData?> {
        return flow {
            val myData = firestore.collection("user").document(email).get().await()
            emit(myData.toObject<UserInfoData>())
        }
    }
}
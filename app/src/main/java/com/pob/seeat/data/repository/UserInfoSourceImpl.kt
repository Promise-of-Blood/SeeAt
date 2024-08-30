package com.pob.seeat.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.pob.seeat.data.model.UserInfoData
import com.pob.seeat.data.remote.UserInfoSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserInfoSourceImpl @Inject constructor(private val firestore: FirebaseFirestore) : UserInfoSource {

    override suspend fun createUserInfo(userInfoData: UserInfoData) {
        firestore.collection("user").document(userInfoData.uid).set(userInfoData).await()
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


}
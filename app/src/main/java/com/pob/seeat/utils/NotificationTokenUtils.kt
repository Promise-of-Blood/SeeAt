package com.pob.seeat.utils

import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber

object NotificationTokenUtils {
    private var token: String? = null
    fun initNotificationToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { success ->
            Timber.d("토큰 : $success")
            token = success
        }
    }
    fun getNotificationToken(): String = token ?: ""
    fun setNotificationToken(newToken: String) {
        token = newToken
    }
}
//
//fun getNotificationToken() : String {
//    var token: String? = null
//    FirebaseMessaging.getInstance().token.addOnSuccessListener { success ->
//        token = success
//    }
//    return token ?: ""
//}


package com.pob.seeat.utils

import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber

object NotificationTokenUtils {
    private var token: String? = null
    fun getNotificationToken() : String {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { success ->
            Timber.d("토큰 : $success")
            token = success
        }
        return token ?: ""
    }
    fun setNotificationToken(newToken: String) {
        token = newToken
    }
}


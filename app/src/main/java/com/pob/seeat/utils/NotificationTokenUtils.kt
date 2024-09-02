package com.pob.seeat.utils

import com.google.firebase.messaging.FirebaseMessaging

fun getNotificationToken() : String {
    var token: String? = null
    FirebaseMessaging.getInstance().token.addOnSuccessListener { success ->
        token = success
    }
    return token ?: ""
}
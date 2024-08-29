//package com.pob.seeat.presentation.service
//
//import android.app.Notification
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.media.RingtoneManager
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import com.google.firebase.messaging.RemoteMessage
//import com.pob.seeat.MainActivity
//import com.pob.seeat.R
//import okhttp3.internal.notify
//
//class TestFirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {
//    val TAG = "FirebaseMsgService"
//    var msg: String = ""
//    var title: String = ""
//
//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//        title = message.notification?.title.toString()
//        msg = message.notification?.body.toString()
//
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java),
//            PendingIntent.FLAG_IMMUTABLE)
//
//        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
//            .setContentTitle(title)
//            .setContentText(msg)
//            .setAutoCancel(true)
//            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//            .setVibrate(longArrayOf(1L, 1000L))
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
//        notificationManager.notify()
//        mBuilder.setContentIntent(contentIntent)
//
//    }
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        Log.d("onNewToken", "Refreshed token: $token")
//    }
//}
package com.pob.seeat.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BigPictureStyle
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pob.seeat.MainActivity
import com.pob.seeat.R
import timber.log.Timber
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Timber.tag("onMessageReceived").d("Content" + message.notification?.imageUrl)

        sendNotification(message)

//        if(message.data.isNotEmpty()){
//            Log.i("바디: ", message.data["body"].toString())
//            Log.i("타이틀: ", message.data["title"].toString())
//            sendNotification(message)
//        }
//
//        else {
//            Log.i("수신에러: ", "data가 비어있습니다. 메시지를 수신하지 못했습니다.")
//            Log.i("data값: ", message.data.toString())
//        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag("onNewToken").d("Refreshed token: " + token)
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        // RequestCode, Id를 고유값으로 지정하여 알림이 개별 표시되도록 함
        val uniId: Int = (System.currentTimeMillis() / 7).toInt()

        // 일회용 PendingIntent
        // PendingIntent : Intent 의 실행 권한을 외부의 어플리케이션에게 위임한다.
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Activity Stack 을 경로만 남긴다. A-B-C-D-B => A-B
        val pendingIntent = PendingIntent.getActivity(this, uniId, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        // 알림 채널 이름
        val channelId = "test_channel"

        // 알림 소리
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val bitmapFromUrl = getBitmapFromURL(remoteMessage.notification?.imageUrl.toString())
        val bigPictureStyle : BigPictureStyle =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                BigPictureStyle().bigPicture(bitmapFromUrl).showBigPictureWhenCollapsed(true)
            else
                BigPictureStyle().bigPicture(bitmapFromUrl)

        // 알림에 대한 UI 정보와 작업을 지정한다.
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // 아이콘 설정
            .setContentTitle(remoteMessage.notification?.title.toString() ?: "(알 수 없음)") // 제목
            .setContentText(remoteMessage.notification?.body.toString() ?: "(알 수 없음)") // 메시지 내용
            .setStyle(bigPictureStyle) // 큰 사진을 위한 스타일 적용
            .setAutoCancel(true)
            .setSound(soundUri) // 알림 소리
            .setPriority(PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // 알림 실행 시 Intent

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 오레오 버전 이후에는 채널이 필요하다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 생성
        notificationManager.notify(uniId, notificationBuilder.build())
    }

    private fun getBitmapFromURL(urlStr: String) : Bitmap {
        val url = URL(urlStr)
        val connection : HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input: InputStream = connection.inputStream
        val bitmap = BitmapFactory.decodeStream(input)
        return bitmap
    }

}
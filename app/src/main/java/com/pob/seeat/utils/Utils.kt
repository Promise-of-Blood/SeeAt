package com.pob.seeat.utils

import android.content.Context
import android.content.res.Resources.getSystem
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.Timestamp
import com.pob.seeat.R
import com.pob.seeat.domain.model.TagModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object Utils {
    /**
     * 현재 시간으로부터 기준 시간과의 차이를 한국어로 반환합니다.
     *
     * - 1분 미만: 방금 전
     * - 1분 이상 1시간 미만: n분 전
     * - 1시간 이상 하루 미만: n시간 전
     * - 하루 이상 일주일 미만: n일 전
     * - 일주일 이상 한달 미만: n주 전
     * - 한달 이상 1년 미만: n달 전
     * - 1년 이상: n년 전
     * */
    fun LocalDateTime.toKoreanDiffString(): String {
        return when (val diffSec = Duration.between(this, LocalDateTime.now()).seconds) {
            in 0..59 -> "방금 전" // ~ 60
            in 60..3599 -> "${diffSec / 60}분 전" // ~ 60 * 60
            in 3600..86399 -> "${diffSec / 3600}시간 전" // ~ 60 * 60 * 24
            in 86400..604799 -> "${diffSec / 86400}일 전" // ~ 60 * 60 * 24 * 7
            in 604800..2419199 -> "${diffSec / 604800}주 전" // ~ 60 * 60 * 24 * 7 * 4
            in 2419200..31535999 -> "${diffSec / 2592000}달 전" // ~ 60 * 60 * 24 * 365
            else -> "${diffSec / 31536000}년 전"
        }
    }

    /**
     * 현재 시간으로부터 기준 시간과의 차이를 한국어로 반환합니다.
     *
     * - 1분 미만: 방금 전
     * - 1분 이상 1시간 미만: n분 전
     * - 1시간 이상 하루 미만: n시간 전
     * - 하루 이상 일주일 미만: n일 전
     * - 일주일 이상 한달 미만: n주 전
     * - 한달 이상 1년 미만: n달 전
     * - 1년 이상: n년 전
     * */
    fun Timestamp.toKoreanDiffString(): String {
        val diffSec = TimeUnit.MILLISECONDS.toSeconds(Date().time - this.toDate().time)
        return when (diffSec) {
            in 0..59 -> "방금 전" // ~ 60
            in 60..3599 -> "${diffSec / 60}분 전" // ~ 60 * 60
            in 3600..86399 -> "${diffSec / 3600}시간 전" // ~ 60 * 60 * 24
            in 86400..604799 -> "${diffSec / 86400}일 전" // ~ 60 * 60 * 24 * 7
            in 604800..2419199 -> "${diffSec / 604800}주 전" // ~ 60 * 60 * 24 * 7 * 4
            in 2419200..31535999 -> "${diffSec / 2592000}달 전" // ~ 60 * 60 * 24 * 365
            else -> "${diffSec / 31536000}년 전"
        }
    }

    /**
     * 천(k), 백만(M) 단위로 표시된 문자열을 반환합니다.
     * */
    fun Int.toFormatShortenedString() = when (this) {
        in 0..999 -> this.toString()
        in 1000..999999 -> String.format(Locale.getDefault(), "%.1fk", this / 1000f)
        in 1000000..999999999 -> "${this / 1000000}M"
        else -> "999M+"
    }

    /**
     * 천(k), 백만(M) 단위로 표시된 문자열을 반환합니다.
     * */
    fun Long.toFormatShortenedString() = when (this) {
        in 0..999 -> this.toString()
        in 1000..999999 -> String.format(Locale.getDefault(), "%.1fk", this / 1000f)
        in 1000000..999999999 -> "${this / 1000000}M"
        else -> "999M+"
    }

    // px to dp
    val Int.dp get() = (this / getSystem().displayMetrics.density).toInt()

    // dp to px
    val Float.px get() = (this * getSystem().displayMetrics.density).toInt()

    fun Timestamp.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofInstant(this.toDate().toInstant(), ZoneId.systemDefault())
    }

    val tagList = listOf(
        TagModel("전체", R.drawable.ic_map),
        TagModel("맛집 추천", R.drawable.ic_soup),
        TagModel("모임", R.drawable.ic_group),
        TagModel("술 친구", R.drawable.ic_beer_strok),
        TagModel("운동 친구", R.drawable.ic_gym),
        TagModel("스터디", R.drawable.ic_pencil),
        TagModel("분실물", R.drawable.ic_lost_item),
        TagModel("정보공유", R.drawable.ic_info),
        TagModel("질문", R.drawable.ic_question),
        TagModel("산책", R.drawable.ic_paw),
        TagModel("밥친구", R.drawable.ic_restaurant),
        TagModel("노래방", R.drawable.ic_microphone_line),
        TagModel("도움", R.drawable.ic_flag),
        TagModel("긴급", R.drawable.ic_megaphone),
        TagModel("기타", R.drawable.ic_sparkles)
    )

    /**
     * 문자열 배열을 미리 정의된 Tag 리스트로 변환합니다.
     * */
    fun List<String>.toTagList(): List<TagModel> {
        val tagList = Utils.tagList
        return this.mapNotNull { tagName ->
            tagList.find { it.tagName == tagName }
        }
    }

    fun compressBitmapToUri(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "compressed_image${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // 품질 80으로 압축
        outputStream.flush()
        outputStream.close()
        return Uri.fromFile(file)
    }


    fun resizeImage(context: Context, imageUri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val exif = ExifInterface(context.contentResolver.openInputStream(imageUri)!!)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        val rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(originalBitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(originalBitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(originalBitmap, 270)
            else -> originalBitmap
        }

        // 이미지 크기를 조정합니다. (예: 800x800 픽셀로 조정)
        return Bitmap.createScaledBitmap(rotatedBitmap, 800, 800, true)
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * 상단 바의 색상을 변경합니다.
     * @param color 변경할 색상
     * */
    fun AppCompatActivity.setStatusBarColor(color: Int) {
        this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.window.statusBarColor = color
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    /**
     * 상단 바의 색상을 투명하게 변경합니다.
     * */
    fun AppCompatActivity.hideStatusBar() {
        this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }
}

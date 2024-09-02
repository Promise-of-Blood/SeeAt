package com.pob.seeat.utils

import android.content.res.Resources.getSystem
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Utils {
    /**
     * 현재 시간으로부터 기준 시간과의 차이를 한국어로 반환합니다.
     *
     * - 0~59초: 방금 전
     * - 60~3599초: n분 전
     * - 3600~86399초: n시간 전
     * - 86400~604799초: n일 전
     * - 이외에는 "yyyy년 MM월 dd일" 형식의 문자열을 반환합니다.
     * */
    fun LocalDateTime.toKoreanDiffString(): String {
        return when (val diffSec = Duration.between(this, LocalDateTime.now()).seconds) {
            in 0..59 -> "방금 전"
            in 60..3599 -> "${diffSec / 60}분 전"
            in 3600..86399 -> "${diffSec / 3600}시간 전"
            in 86400..604799 -> "${diffSec / 86400}일 전"
            else -> this.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
        }
    }

    // px to dp
    val Int.dp get() = (this / getSystem().displayMetrics.density).toInt()

    // dp to px
    val Float.px get() = (this * getSystem().displayMetrics.density).toInt()

    fun Timestamp.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofInstant(this.toDate().toInstant(), ZoneId.systemDefault())
    }

    val tagList = listOf(
        Tag("전체", R.drawable.ic_map, Color.parseColor("#2ECC87")),
        Tag("맛집 추천", R.drawable.ic_soup, Color.parseColor("#FFCF30")),
        Tag("모임", R.drawable.ic_group, Color.parseColor("#A2FF77")),
        Tag("술 친구", R.drawable.ic_beer_strok, Color.parseColor("#2ECC87")),
        Tag("운동 친구", R.drawable.ic_gym, Color.parseColor("#2ECC87")),
        Tag("스터디", R.drawable.ic_pencil, Color.parseColor("#FF9500")),
        Tag("분실물", R.drawable.ic_lost_item, Color.parseColor("#FFAA75")),
        Tag("정보공유", R.drawable.ic_info, Color.parseColor("#5145FF")),
        Tag("질문", R.drawable.ic_question, Color.parseColor("#717171")),
        Tag("산책", R.drawable.ic_paw, Color.parseColor("#FF9CE1")),
        Tag("밥친구", R.drawable.ic_restaurant, Color.parseColor("#FFC300")),
        Tag("노래방", R.drawable.ic_microphone_line, Color.parseColor("#9A7EFF")),
        Tag("도움", R.drawable.ic_flag, Color.parseColor("#5196FF")),
        Tag("긴급", R.drawable.ic_megaphone, Color.parseColor("#FF3939")),
        Tag("기타", R.drawable.ic_sparkles, Color.parseColor("#FFDF60"))
    )
}

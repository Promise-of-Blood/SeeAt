package com.pob.seeat.utils

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
}

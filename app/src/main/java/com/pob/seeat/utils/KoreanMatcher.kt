package com.pob.seeat.utils

object KoreanMatcher {
    private const val KOREAN_UNICODE_START = 44032 // 가
    private const val KOREAN_UNICODE_END = 55203   // 힣
    private const val KOREAN_UNICODE_BASED = 588   // 각 자음 마다 가지는 글자 수
    private const val KOREAN_JONGSEONG_COUNT = 28 // 받침의 개수
    private val koreanConsonant = arrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
        'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    ) // 자음

    // 초성인지 체크
    private fun isConsonant(char: Char) = koreanConsonant.contains(char)

    // 한글인지 체크
    private fun isKorean(char: Char) = char.code in KOREAN_UNICODE_START..KOREAN_UNICODE_END

    // 자음 얻기
    private fun getConsonant(char: Char): Char {
        val hasBegin = (char.code - KOREAN_UNICODE_START)
        val idx = hasBegin / KOREAN_UNICODE_BASED
        return koreanConsonant[idx]
    }

    // 받침 제거
    private fun removeJongseong(char: Char): Char {
        if (!isKorean(char)) return char // 한글이 아닌 경우 그대로 반환

        val idx = (char.code - KOREAN_UNICODE_START) % KOREAN_JONGSEONG_COUNT
        return if (idx == 0) {
            char // 받침이 없는 경우 그대로 반환
        } else {
            // 받침이 있는 경우 받침을 제거한 문자 반환
            ((char.code - idx).toChar())
        }
    }

    /**
     * 초성 또는 한글 검색
     * @param based 비교 대상
     * @param search 검색 단어
     */
    fun matchKoreanAndConsonant(based: String, search: String): Boolean {
        var temp: Int
        val diffLength = based.length - search.length
        val searchLength = search.length

        if (diffLength < 0) {
            return false
        } else {
            for (i in 0..diffLength) {
                temp = 0

                while (temp < searchLength) {
                    val basedChar = removeJongseong(based[i + temp])

                    // 현재 char이 초성이고 based가 한글일 때
                    if (isConsonant(search[temp]) && isKorean(based[i + temp])) {
                        // 각각의 초성끼리 같은지 비교
                        if (getConsonant(based[i + temp]) == search[temp]) {
                            temp++
                        } else {
                            break
                        }
                    } else {
                        // char이 일반 문자라면
                        // 문자가 같거나, 받침을 제외한 문자가 같은지 비교
                        if (based[i + temp] == search[temp] || basedChar == search[temp]) {
                            temp++
                        } else {
                            break
                        }
                    }
                }
                if (temp == searchLength) return true
            }
            return false
        }
    }
}

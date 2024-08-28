package com.pob.seeat.domain.model

enum class SampleType(val viewType: Int) {
    IMAGE(0),
    VIDEO(1),
    DEFAULT(-1)
}

//서버에서 내려오는 값을 enum으로 변경
fun findSampleType(viewType: Int): SampleType {
    return SampleType.entries.find { viewType == it.viewType } ?: SampleType.DEFAULT
}
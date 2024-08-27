package com.pob.seeat.domain.model

import com.pob.seeat.data.remote.VideoDocumentResponse
import com.pob.seeat.data.remote.ImageDocumentResponse
import java.util.Date
import java.util.UUID

data class SampleModel(
    val thumbnailUrl: String?,
    val isFavorite: Boolean = false,
    val dateTime: Date?,
    val uId: String = UUID.randomUUID().toString(),
    val type: SampleType
)

fun toImageUser(target: List<ImageDocumentResponse>): List<SampleModel> = with(target) {
    return map { sample ->
        SampleModel(
            thumbnailUrl = sample.thumbnailUrl,
            type = SampleType.IMAGE,
            dateTime = sample.datetime
        )
    }
}

fun toVideoUser(target: List<VideoDocumentResponse>): List<SampleModel> = with(target) {
    return map { sample ->
        SampleModel(
            thumbnailUrl = sample.thumbnail,
            type = SampleType.VIDEO,
            dateTime = sample.datetime
        )
    }
}
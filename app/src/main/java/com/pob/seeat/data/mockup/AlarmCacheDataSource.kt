package com.pob.seeat.data.mockup

import com.pob.seeat.data.model.AlarmResponse
import java.time.LocalDateTime
import kotlin.random.Random

object AlarmCacheDataSource {
    private var alarmList = alarmList()

    fun getAlarmList(): List<AlarmResponse> {
        return alarmList
    }

    fun setAlarmList(alarmList: List<AlarmResponse>) {
        this.alarmList = alarmList
    }
}

fun alarmList(): List<AlarmResponse> {
    return listOf(
        AlarmResponse(
            postId = "somerandomid0",
            postTitle = "으아 어이없어",
            postImage = "https://picsum.photos/200",
            content = "나 공명선인데 나 140 맞다",
            createdAt = LocalDateTime.now().withNano(Random.nextInt(100000)),
            isRead = listOf(true, false).random(),
        ),
        AlarmResponse(
            postId = "somerandomid1",
            postTitle = "으아 어이없어",
            postImage = "https://picsum.photos/200",
            content = "나 공명선인데 나 140 맞다",
            createdAt = LocalDateTime.now().withNano(Random.nextInt(100000)),
            isRead = listOf(true, false).random(),
        ),
        AlarmResponse(
            postId = "somerandomid2",
            postTitle = "으아 어이없어",
            postImage = "https://picsum.photos/200",
            content = "나 공명선인데 나 140 맞다",
            createdAt = LocalDateTime.now().withNano(Random.nextInt(100000)),
            isRead = listOf(true, false).random(),
        ),
        AlarmResponse(
            postId = "somerandomid3",
            postTitle = "으아 어이없어",
            postImage = "https://picsum.photos/200",
            content = "나 공명선인데 나 140 맞다",
            createdAt = LocalDateTime.now().withNano(Random.nextInt(100000)),
            isRead = listOf(true, false).random(),
        ),
        AlarmResponse(
            postId = "somerandomid4",
            postTitle = "으아 어이없어",
            postImage = "https://picsum.photos/200",
            content = "나 공명선인데 나 140 맞다",
            createdAt = LocalDateTime.now().withNano(Random.nextInt(100000)),
            isRead = listOf(true, false).random(),
        ),
        AlarmResponse(
            postId = "somerandomid5",
            postTitle = "으아 어이없어",
            postImage = "https://picsum.photos/200",
            content = "나 공명선인데 나 140 맞다",
            createdAt = LocalDateTime.now().withNano(Random.nextInt(100000)),
            isRead = listOf(true, false).random(),
        ),
        AlarmResponse(
            postId = "somerandomid6",
            postTitle = "으아 어이없어",
            postImage = "https://picsum.photos/200",
            content = "나 공명선인데 나 140 맞다",
            createdAt = LocalDateTime.now().withNano(Random.nextInt(100000)),
            isRead = listOf(true, false).random(),
        ),
        AlarmResponse(
            postId = "somerandomid7",
            postTitle = "으아 어이없어",
            postImage = "https://picsum.photos/200",
            content = "나 공명선인데 나 140 맞다",
            createdAt = LocalDateTime.now().withNano(Random.nextInt(100000)),
            isRead = listOf(true, false).random(),
        ),
        AlarmResponse(
            postId = "somerandomid8",
            postTitle = "으아 어이없어",
            postImage = "https://picsum.photos/200",
            content = "나 공명선인데 나 140 맞다",
            createdAt = LocalDateTime.now().withNano(Random.nextInt(100000)),
            isRead = listOf(true, false).random(),
        ),
        AlarmResponse(
            postId = "somerandomid9",
            postTitle = "으아 어이없어",
            postImage = "https://picsum.photos/200",
            content = "나 공명선인데 나 140 맞다",
            createdAt = LocalDateTime.now().withNano(Random.nextInt(100000)),
            isRead = listOf(true, false).random(),
        ),
        AlarmResponse(
            postId = "somerandomid10",
            postTitle = "으아 어이없어",
            postImage = "https://picsum.photos/200",
            content = "나 공명선인데 나 140 맞다",
            createdAt = LocalDateTime.now().withNano(Random.nextInt(100000)),
            isRead = listOf(true, false).random(),
        ),
    )
}
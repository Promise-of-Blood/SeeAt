package com.pob.seeat.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    /**
     * replay -> collector 가 연결됐을 때, 전달받을 이전 데이터의 개수를 지정한다.
     *     0일 경우, collect 이후의 데이터부터 전달받고,
     *     1일 경우, collect 직전의 데이터부터 전달받으며 시작한다.
     */
    private val _events = MutableSharedFlow<Int>(replay = 0)
    val events = _events.asSharedFlow()

    suspend fun post(event: Int) {
        _events.emit(event)
    }

}
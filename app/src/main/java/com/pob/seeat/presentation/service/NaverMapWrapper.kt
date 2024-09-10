package com.pob.seeat.presentation.service

import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class NaverMapWrapper @Inject constructor() {

    private var naverMap: NaverMap? = null
    private val naverMapFlow = MutableStateFlow<NaverMap?>(null)

    private var resumeCallback: ((NaverMap) -> Unit)? = null

    fun initialize(mapFragment: MapFragment) {
        mapFragment.getMapAsync { map ->
            naverMap = map
            naverMapFlow.value = map
        }
    }

    fun setOnResumeCallback(callback: (NaverMap) -> Unit) {
        resumeCallback = callback
        naverMap?.let { callback(it) }  // 이미 naverMap이 초기화된 경우 즉시 콜백 실행
    }

    fun getNaverMap(): StateFlow<NaverMap?> = naverMapFlow.asStateFlow()
}
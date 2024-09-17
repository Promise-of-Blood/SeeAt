package com.pob.seeat.domain.model

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey

/**
 * 클러스터 생성을 위한 ItemKey 정의
 *
 * `id` 와 `position`을 가지는 클래스
 * `getPosition()`으로 `LatLng` 클래스의 좌표값 반환
 */
class ItemKey(val id: String, private val position: LatLng) : ClusteringKey {
    override fun getPosition() = position

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val itemKey = other as ItemKey
        return id == itemKey.id
    }
    override fun hashCode() = id.hashCode()
}
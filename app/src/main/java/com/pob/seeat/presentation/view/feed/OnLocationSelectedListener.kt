package com.pob.seeat.presentation.view.feed

import com.naver.maps.geometry.LatLng

interface OnLocationSelectedListener {
    fun onLocationSelected(location: LatLng)
}
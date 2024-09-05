package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.pob.seeat.presentation.view.home.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class NewFeedViewModel : ViewModel() {
    // MutableStateFlow를 사용하여 상태 관리
    private val _selectTagList = MutableStateFlow<List<Tag>>(emptyList())
    val selectTagList: StateFlow<List<Tag>> = _selectTagList.asStateFlow()

    var selectLocation: LatLng? = null


    fun updateSelectTagList(newList: List<Tag>) {
        viewModelScope.launch {
            _selectTagList.value = newList
        }
        Timber.tag("NewFeedViewModel").d("updateSelectTagList: $newList")
    }

    fun updateSelectLocation(newLocation: LatLng) {
        viewModelScope.launch {
            selectLocation = newLocation
        }
        Timber.tag("NewFeedViewModel").d("updateSelectLocation: $newLocation")

    }

}
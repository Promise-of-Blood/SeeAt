package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MyPageSharedViewModel : ViewModel() {
    private val _triggerEvent = MutableSharedFlow<Unit>(replay = 0)
    val triggerEvent: SharedFlow<Unit> get() = _triggerEvent

    fun triggerRefresh() {
        viewModelScope.launch {
            _triggerEvent.emit(Unit)
        }
    }
}
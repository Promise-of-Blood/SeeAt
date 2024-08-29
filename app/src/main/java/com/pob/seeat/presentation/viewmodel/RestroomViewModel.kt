package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.domain.model.RestroomModel
import com.pob.seeat.domain.usecase.RestroomApiUseCase
import com.pob.seeat.presentation.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestroomViewModel @Inject constructor(
    private val restroomApiUseCase: RestroomApiUseCase
) : ViewModel() {
    private val _restroomUiState = MutableStateFlow<UiState<List<RestroomModel>>>(UiState.Loading)
    val restroomUiState : StateFlow<UiState<List<RestroomModel>>> get() = _restroomUiState

    fun getRestroomUiState() {
        viewModelScope.launch {
            _restroomUiState.value = UiState.Loading
            restroomApiUseCase()
                .flowOn(Dispatchers.IO)
                .catch { error ->
                    _restroomUiState.value = UiState.Error(error.message ?: "Unknown Error")
                }
                .collect { list ->
                    _restroomUiState.value = UiState.Success(list)
                }
        }
    }
}
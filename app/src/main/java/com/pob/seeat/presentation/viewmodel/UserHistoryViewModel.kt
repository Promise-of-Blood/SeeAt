package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.usecase.UserHistoryUseCase
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.mypage.items.HistoryListItem
import com.pob.seeat.presentation.view.mypage.items.toHistoryListItemList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserHistoryViewModel @Inject constructor(private val userHistoryUseCase: UserHistoryUseCase) : ViewModel() {
    private val _history = MutableStateFlow<UiState<List<HistoryListItem>>>(UiState.Loading)
    val history : StateFlow<UiState<List<HistoryListItem>>> get() = _history

    fun getUserFeedHistory() {
        viewModelScope.launch {
            _history.value = UiState.Loading
            userHistoryUseCase.execute()
                .flowOn(Dispatchers.IO)
                .catch { error ->
                    _history.value = UiState.Error(error.message ?: "Unknown Error")
                }
                .collect {
                    when (it) {
                        is Result.Loading -> _history.value = UiState.Loading
                        is Result.Error -> _history.value = UiState.Error(it.message)
                        is Result.Success -> _history.value = UiState.Success(it.data.toHistoryListItemList())
                    }
                }
        }
    }
}
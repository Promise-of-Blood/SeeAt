package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.presentation.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.domain.usecase.GetFeedListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn


@HiltViewModel
class FeedListViewModel @Inject constructor(private val getFeedListUseCase: GetFeedListUseCase) :
    ViewModel() {

    private val _feedListUiState = MutableStateFlow<UiState<List<FeedModel>>>(UiState.Loading)
    val feedListUiState: StateFlow<UiState<List<FeedModel>>> get() = _feedListUiState

    fun getFeedListUiState(query: String) {
        viewModelScope.launch {

            _feedListUiState.value = UiState.Loading
            getFeedListUseCase(query)
                .flowOn(Dispatchers.IO)
                .catch { error ->
                    _feedListUiState.value = UiState.Error(error.toString())
                }
                .collect { result ->
                    _feedListUiState.value = when (result) {
                        is Result.Success -> UiState.Success(result.data)
                        is Result.Loading -> UiState.Loading
                        is Result.Error -> UiState.Error(result.message)
                    }
                }
        }
    }
}
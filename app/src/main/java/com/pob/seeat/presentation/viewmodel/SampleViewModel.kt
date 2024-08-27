package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.domain.model.SampleModel
import com.pob.seeat.domain.repository.SampleRepository
import com.pob.seeat.presentation.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SampleViewModel @Inject constructor(
    private val sampleRepository: SampleRepository
) : ViewModel() {

    private val _sampleUiState = MutableStateFlow<UiState<List<SampleModel>>>(UiState.Loading)
    val sampleUiState: StateFlow<UiState<List<SampleModel>>> = _sampleUiState

    //
    fun getSampleImageList(query: String) {
        viewModelScope.launch {
            sampleRepository.searchUserImageList(query)
                .zip(sampleRepository.searchUserVideoList(query)) { imageList, vidioList ->
                    return@zip (imageList + vidioList).sortedByDescending { it.dateTime }
                }.flowOn(Dispatchers.IO).catch { error ->
                    _sampleUiState.value = UiState.Error(error.toString())
                }.collect {
                    _sampleUiState.value = UiState.Success(it)
                }
        }
    }
}
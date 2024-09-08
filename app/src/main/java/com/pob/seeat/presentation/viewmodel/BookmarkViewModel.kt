package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.BookmarkModel
import com.pob.seeat.domain.model.toBookmarkModelList
import com.pob.seeat.domain.usecase.FetchBookmarkListUseCase
import com.pob.seeat.domain.usecase.GetBookmarkListUseCase
import com.pob.seeat.domain.usecase.SaveBookmarkListUseCase
import com.pob.seeat.domain.usecase.SaveBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getBookmarkListUseCase: GetBookmarkListUseCase,
    private val saveBookmarkListUseCase: SaveBookmarkListUseCase,
    private val saveBookmarkUseCase: SaveBookmarkUseCase,
    private val fetchBookmarkListUseCase: FetchBookmarkListUseCase,
) : ViewModel() {
    private val _bookmarkList = MutableStateFlow<Result<List<BookmarkModel>>>(Result.Loading)
    val bookmarkList: StateFlow<Result<List<BookmarkModel>>> get() = _bookmarkList

    private fun getBookmarkList() {
        viewModelScope.launch {
            getBookmarkListUseCase().collect { _bookmarkList.value = it }
        }
    }

    fun fetchBookmarkList(feedIdList: List<String>) {
        viewModelScope.launch {
            fetchBookmarkListUseCase(feedIdList).collect {
                when (it) {
                    is Result.Loading -> _bookmarkList.value = Result.Loading
                    is Result.Error -> _bookmarkList.value = Result.Error(it.message)
                    is Result.Success -> {
                        _bookmarkList.value = Result.Success(it.data.toBookmarkModelList())
                        saveBookmarkList(it.data.toBookmarkModelList())
                    }
                }
            }
        }
    }

    private fun saveBookmarkList(list: List<BookmarkModel>) {
        viewModelScope.launch {
            saveBookmarkListUseCase(list)
        }
    }

    init {
        getBookmarkList()
    }
}
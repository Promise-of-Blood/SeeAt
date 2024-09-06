package com.pob.seeat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pob.seeat.data.model.Result
import com.pob.seeat.domain.model.BookmarkModel
import com.pob.seeat.domain.usecase.DeleteBookmarkUseCase
import com.pob.seeat.domain.usecase.GetBookmarkListUseCase
import com.pob.seeat.domain.usecase.SaveBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val getBookmarkListUseCase: GetBookmarkListUseCase,
    private val saveBookmarkUseCase: SaveBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
) : ViewModel() {
    private val _bookmarkList = MutableStateFlow<Result<List<BookmarkModel>>>(Result.Loading)
    val bookmarkList: StateFlow<Result<List<BookmarkModel>>> get() = _bookmarkList

    fun getBookmarkList() {
        viewModelScope.launch {
            getBookmarkListUseCase().collect { _bookmarkList.value = it }
        }
    }

    fun saveBookmark(feed: BookmarkModel) {
        viewModelScope.launch {
            saveBookmarkUseCase(feed)
        }
    }

    fun deleteBookmark(feedId: String) {
        viewModelScope.launch {
            deleteBookmarkUseCase(feedId)
        }
    }

    init {
        getBookmarkList()
    }
}
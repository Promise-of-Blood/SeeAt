package com.pob.seeat.presentation.view.chat.chatlist

sealed interface ChatListUiState<out T> {

    data class Success<T>(val data : T) : ChatListUiState<T>

    data class Error(val message : String) : ChatListUiState<Nothing>

    object Loading : ChatListUiState<Nothing>

    object Empty : ChatListUiState<Nothing>
}
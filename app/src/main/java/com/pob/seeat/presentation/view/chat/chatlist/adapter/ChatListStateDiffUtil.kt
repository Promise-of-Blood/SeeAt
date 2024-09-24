package com.pob.seeat.presentation.view.chat.chatlist.adapter

import androidx.recyclerview.widget.DiffUtil
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem

class ChatListStateDiffUtil : DiffUtil.ItemCallback<UiState<List<Result<ChatListUiItem>>>>() {
    override fun areItemsTheSame(
        oldItem: UiState<List<Result<ChatListUiItem>>>,
        newItem: UiState<List<Result<ChatListUiItem>>>,
    ): Boolean {
        when(oldItem) {
            is UiState.Success -> {

            }
            is UiState.Error -> {

            }
            is UiState.Loading -> {

            }
        }
        return oldItem == newItem
    }
    override fun areContentsTheSame(
        oldItem: UiState<List<Result<ChatListUiItem>>>,
        newItem: UiState<List<Result<ChatListUiItem>>>,
    ): Boolean {
        return oldItem == newItem
    }
}
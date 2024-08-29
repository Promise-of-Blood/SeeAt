package com.pob.seeat.presentation.view.chat.adapter

import androidx.recyclerview.widget.DiffUtil
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem

class ChatListDiffUtil : DiffUtil.ItemCallback<UiState<ChatListUiItem>>() {
    override fun areItemsTheSame(
        oldItem: UiState<ChatListUiItem>,
        newItem: UiState<ChatListUiItem>
    ): Boolean {
        return if(oldItem is UiState.Success && newItem is UiState.Success) oldItem.data == newItem.data
        else oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: UiState<ChatListUiItem>,
        newItem: UiState<ChatListUiItem>
    ): Boolean {
        return oldItem == newItem
    }
}
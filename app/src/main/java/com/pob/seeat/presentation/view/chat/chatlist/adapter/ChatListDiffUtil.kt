package com.pob.seeat.presentation.view.chat.chatlist.adapter

import androidx.recyclerview.widget.DiffUtil
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
import com.pob.seeat.data.model.Result

class ChatListDiffUtil : DiffUtil.ItemCallback<Result<ChatListUiItem>>() {
    override fun areItemsTheSame(
        oldItem: Result<ChatListUiItem>,
        newItem: Result<ChatListUiItem>
    ): Boolean {
        return if(oldItem is Result.Success && newItem is Result.Success) oldItem.data == newItem.data
        else oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Result<ChatListUiItem>,
        newItem: Result<ChatListUiItem>
    ): Boolean {
        return oldItem == newItem
    }
}
package com.pob.seeat.presentation.view.chat.chatlist.adapter

import androidx.recyclerview.widget.DiffUtil
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
import com.pob.seeat.data.model.Result
import timber.log.Timber

class ChatListDiffUtil : DiffUtil.ItemCallback<Result<ChatListUiItem>>() {
    override fun areItemsTheSame(
        oldItem: Result<ChatListUiItem>,
        newItem: Result<ChatListUiItem>
    ): Boolean {
        return if(oldItem is Result.Success && newItem is Result.Success) {
            if(oldItem.data.id == newItem.data.id) {
                Timber.d("diffutilS areItemsTheSame: true")
            } else Timber.d("diffutilS areItemsTheSame: false")
            oldItem.data.id == newItem.data.id
        } else oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Result<ChatListUiItem>,
        newItem: Result<ChatListUiItem>
    ): Boolean {
        if(oldItem == newItem) Timber.d("diffutilS areContentsTheSame: true")
        else Timber.d("diffutilS areContentsTheSame: false")
        return if(oldItem is Result.Success && newItem is Result.Success) {
            (oldItem.data.id == newItem.data.id)
                    && (oldItem.data.content == newItem.data.content)
                    && (oldItem.data.feedFrom == newItem.data.feedFrom)
        } else oldItem == newItem
    }
}
package com.pob.seeat.presentation.view.chat.adapter

import androidx.recyclerview.widget.DiffUtil
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem

class ChattingDiffUtil : DiffUtil.ItemCallback<UiState<ChattingUiItem>>() {
    override fun areItemsTheSame(
        oldItem: UiState<ChattingUiItem>,
        newItem: UiState<ChattingUiItem>
    ): Boolean {
        return if(oldItem is UiState.Success && newItem is UiState.Success) oldItem.data == newItem.data
        else oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: UiState<ChattingUiItem>,
        newItem: UiState<ChattingUiItem>
    ): Boolean {
        return oldItem == newItem
    }
}
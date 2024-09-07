package com.pob.seeat.presentation.view.chat.adapter

import androidx.recyclerview.widget.DiffUtil
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem
import com.pob.seeat.data.model.Result

class ChattingDiffUtil : DiffUtil.ItemCallback<Result<ChattingUiItem>>() {
    override fun areItemsTheSame(
        oldItem: Result<ChattingUiItem>,
        newItem: Result<ChattingUiItem>
    ): Boolean {
        return if(oldItem is Result.Success && newItem is Result.Success) oldItem.data == newItem.data
        else oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Result<ChattingUiItem>,
        newItem: Result<ChattingUiItem>
    ): Boolean {
        return oldItem == newItem
    }
}
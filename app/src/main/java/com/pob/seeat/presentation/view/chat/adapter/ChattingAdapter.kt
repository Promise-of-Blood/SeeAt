package com.pob.seeat.presentation.view.chat.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem
import com.pob.seeat.presentation.view.chat.viewholder.ChattingViewHolder

class ChattingAdapter : ListAdapter<UiState<ChattingUiItem>, ChattingViewHolder>(
    ChattingDiffUtil()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ChattingViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemViewType(position: Int): Int {
        TODO("아이템 뷰타입을 success 이후, 한 번 더 나눠야 할 듯?")
    }

}
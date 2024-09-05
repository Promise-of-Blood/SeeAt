package com.pob.seeat.presentation.view.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pob.seeat.databinding.ItemChattingMessageMeBinding
import com.pob.seeat.databinding.ItemChattingMessageYouBinding
import com.pob.seeat.databinding.ItemOnlyTimeBinding
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.chat.chatlist.adapter.ChattingViewType
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem
import com.pob.seeat.presentation.view.chat.viewholder.ChattingOnlyTimeViewHolder
import com.pob.seeat.presentation.view.chat.viewholder.ChattingViewHolder
import com.pob.seeat.presentation.view.chat.viewholder.MyChattingSuccessViewHolder
import com.pob.seeat.presentation.view.chat.viewholder.YourChattingSuccessViewHolder

class ChattingAdapter : ListAdapter<UiState<ChattingUiItem>, ChattingViewHolder>(
    ChattingDiffUtil()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingViewHolder {
        val chattingViewType = ChattingViewType.entries.find { it.viewType == viewType }
        return when(chattingViewType) {
            ChattingViewType.EMPTY -> TODO()
            ChattingViewType.MY_CHAT -> MyChattingSuccessViewHolder(ItemChattingMessageMeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            ChattingViewType.YOUR_CHAT -> YourChattingSuccessViewHolder(ItemChattingMessageYouBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            ChattingViewType.ONLY_TIME -> ChattingOnlyTimeViewHolder(ItemOnlyTimeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            null -> TODO()
        }
    }

    override fun onBindViewHolder(holder: ChattingViewHolder, position: Int) {
        val nowData = getItem(position)
        if(nowData is UiState.Success) holder.bind(nowData.data)
    }

    override fun getItemViewType(position: Int): Int {
        when(val nowData = getItem(position)) {
            is UiState.Success -> {
                return when(nowData.data) {
                    is ChattingUiItem.MyChatItem -> ChattingViewType.MY_CHAT.viewType
                    is ChattingUiItem.YourChatItem -> ChattingViewType.YOUR_CHAT.viewType
                    is ChattingUiItem.OnlyTimeItem -> ChattingViewType.ONLY_TIME.viewType
                }
            }
            is UiState.Error -> TODO()
            UiState.Loading -> TODO()
        }
    }

}
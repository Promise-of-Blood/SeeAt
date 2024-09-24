package com.pob.seeat.presentation.view.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.pob.seeat.databinding.ItemChattingMessageMeBinding
import com.pob.seeat.databinding.ItemChattingMessageYouBinding
import com.pob.seeat.databinding.ItemOnlyTimeBinding
import com.pob.seeat.presentation.view.chat.chatlist.adapter.ChattingViewType
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem
import com.pob.seeat.presentation.view.chat.viewholder.ChattingOnlyTimeViewHolder
import com.pob.seeat.presentation.view.chat.viewholder.ChattingViewHolder
import com.pob.seeat.presentation.view.chat.viewholder.MyChattingSuccessViewHolder
import com.pob.seeat.presentation.view.chat.viewholder.YourChattingSuccessViewHolder
import com.pob.seeat.data.model.Result

class ChattingAdapter : ListAdapter<Result<ChattingUiItem>, ChattingViewHolder>(
    ChattingDiffUtil()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingViewHolder {
        val chattingViewType = ChattingViewType.entries.find { it.viewType == viewType } ?: ChattingViewType.EMPTY
        return when(chattingViewType) {
            ChattingViewType.EMPTY -> TODO()
            ChattingViewType.MY_CHAT -> MyChattingSuccessViewHolder(ItemChattingMessageMeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            ChattingViewType.YOUR_CHAT -> YourChattingSuccessViewHolder(ItemChattingMessageYouBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            ChattingViewType.ONLY_TIME -> ChattingOnlyTimeViewHolder(ItemOnlyTimeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: ChattingViewHolder, position: Int) {
        val nowData = getItem(position)
        if(nowData is Result.Success) holder.bind(nowData.data)
    }

    override fun getItemViewType(position: Int): Int {
        when(val nowData = getItem(position)) {
            is Result.Success -> {
                return when(nowData.data) {
                    is ChattingUiItem.MyChatItem -> ChattingViewType.MY_CHAT.viewType
                    is ChattingUiItem.YourChatItem -> ChattingViewType.YOUR_CHAT.viewType
                    is ChattingUiItem.OnlyTimeItem -> ChattingViewType.ONLY_TIME.viewType
                }
            }
            is Result.Error -> TODO()
            Result.Loading -> TODO()
        }
    }

}
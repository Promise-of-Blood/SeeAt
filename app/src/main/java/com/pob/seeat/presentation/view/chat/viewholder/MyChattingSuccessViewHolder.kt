package com.pob.seeat.presentation.view.chat.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.pob.seeat.databinding.ItemChattingMessageMeBinding
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem

class MyChattingSuccessViewHolder(private val binding: ItemChattingMessageMeBinding) : ChattingViewHolder(binding) {
    override fun bind(item: ChattingUiItem) {
        if (item is ChattingUiItem.MyChatItem) {
            binding.tvMyChat.text = item.message
        }
    }
}
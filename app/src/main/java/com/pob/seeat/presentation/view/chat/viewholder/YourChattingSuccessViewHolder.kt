package com.pob.seeat.presentation.view.chat.viewholder

import com.pob.seeat.databinding.ItemChattingMessageYouBinding
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem

class YourChattingSuccessViewHolder(private val binding: ItemChattingMessageYouBinding) : ChattingViewHolder(binding) {
    override fun bind(item: ChattingUiItem) {
        if (item is ChattingUiItem.YourChatItem) {
            binding.tvYourChat.text = item.message
        }
    }
}
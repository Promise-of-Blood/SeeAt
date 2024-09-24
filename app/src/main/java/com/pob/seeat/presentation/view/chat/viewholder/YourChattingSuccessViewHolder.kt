package com.pob.seeat.presentation.view.chat.viewholder

import androidx.core.view.isVisible
import com.pob.seeat.databinding.ItemChattingMessageYouBinding
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem
import com.pob.seeat.utils.Utils.toKoreanDateHourMinute

class YourChattingSuccessViewHolder(private val binding: ItemChattingMessageYouBinding) : ChattingViewHolder(binding) {
    override fun bind(item: ChattingUiItem) {
        if (item is ChattingUiItem.YourChatItem) {
            binding.apply {
                tvYourChat.text = item.message
                tvYourChatTimestamp.text = item.time.toKoreanDateHourMinute()
                tvYourChatTimestamp.isVisible = item.isShowTime
            }
        }
    }
}
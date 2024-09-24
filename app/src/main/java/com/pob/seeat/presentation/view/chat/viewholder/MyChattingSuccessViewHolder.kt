package com.pob.seeat.presentation.view.chat.viewholder

import androidx.core.view.isVisible
import com.pob.seeat.databinding.ItemChattingMessageMeBinding
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem
import com.pob.seeat.utils.Utils.toKoreanDateHourMinute

class MyChattingSuccessViewHolder(private val binding: ItemChattingMessageMeBinding) : ChattingViewHolder(binding) {
    override fun bind(item: ChattingUiItem) {
        if (item is ChattingUiItem.MyChatItem) {
            binding.apply {
                tvMyChat.text = item.message
                tvMyChatTimestamp.text = item.time.toKoreanDateHourMinute()
                tvMyChatTimestamp.isVisible = item.isShowTime
            }
        }
    }
}
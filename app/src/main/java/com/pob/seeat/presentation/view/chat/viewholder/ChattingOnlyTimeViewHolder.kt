package com.pob.seeat.presentation.view.chat.viewholder

import com.pob.seeat.databinding.ItemOnlyTimeBinding
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem
import com.pob.seeat.utils.Utils.toKoreanDateYearMonthDay

class ChattingOnlyTimeViewHolder(private val binding: ItemOnlyTimeBinding) : ChattingViewHolder(binding) {
    override fun bind(item: ChattingUiItem) {
        if (item is ChattingUiItem.OnlyTimeItem) {
            binding.tvOnlyTime.text = item.time.toKoreanDateYearMonthDay()
        }
    }
}
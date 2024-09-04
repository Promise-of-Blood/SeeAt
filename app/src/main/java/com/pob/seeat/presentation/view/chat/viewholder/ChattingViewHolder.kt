package com.pob.seeat.presentation.view.chat.viewholder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pob.seeat.presentation.view.chat.items.ChattingUiItem

abstract class ChattingViewHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: ChattingUiItem)
}
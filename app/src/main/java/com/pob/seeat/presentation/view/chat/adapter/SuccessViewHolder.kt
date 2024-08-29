package com.pob.seeat.presentation.view.chat.adapter

import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pob.seeat.databinding.ItemChatListBinding
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem

class SuccessViewHolder(private val binding: ItemChatListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ChatListUiItem.ChatItem) {
        binding.apply {
            root.setOnClickListener {
                Log.d("ChatList", "챗 리스트 클릭 ${binding.tvChatListItem}")
            }
            tvChatListItem.text = item.person
            tvChatListItemTime.text = item.lastTime
            tvChatListItemContent.text = item.content
            tvChatListItemCount.text = item.unreadMessageCount.toString()
        }
    }
}
package com.pob.seeat.presentation.view.chat.chatlist.viewholder

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.pob.seeat.databinding.ItemChatListBinding
import com.pob.seeat.presentation.view.chat.chatlist.adapter.ChatListAdapter
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem

class SuccessViewHolder(private val binding: ItemChatListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ChatListUiItem, clickListener: ChatListAdapter.ClickListener?) {
        binding.apply {
            root.setOnClickListener {
                Log.d("ChatList", "챗 리스트 클릭 ${binding.tvChatListItem}")
                clickListener?.onClick(item)
            }
            tvChatListItem.text = item.person
            tvChatListItemTime.text = item.lastTime
            tvChatListItemContent.text = item.content
            tvChatListItemCount.text = item.unreadMessageCount.toString()
        }
    }
}
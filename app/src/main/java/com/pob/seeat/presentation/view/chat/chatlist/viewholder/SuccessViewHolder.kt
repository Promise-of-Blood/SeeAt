package com.pob.seeat.presentation.view.chat.chatlist.viewholder

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.pob.seeat.databinding.ItemChatListBinding
import com.pob.seeat.presentation.view.chat.chatlist.adapter.ChatListAdapter
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
import com.pob.seeat.utils.Utils.toKoreanDiffString

class SuccessViewHolder(private val binding: ItemChatListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ChatListUiItem, clickListener: ChatListAdapter.ClickListener?) {
        binding.apply {
            root.setOnClickListener {
                Log.d("ChatList", "챗 리스트 클릭 ${binding.tvChatListItem}")
                clickListener?.onClick(item)
            }
            tvChatListItem.text = item.person
            tvChatListItemTime.text = Timestamp(item.lastTime / 1000, ((item.lastTime % 1000L) * 1000000L).toInt()).toKoreanDiffString()
            tvChatListItemContent.text = item.content
            tvChatListItemCount.text = item.unreadMessageCount.toString()
        }
    }
}
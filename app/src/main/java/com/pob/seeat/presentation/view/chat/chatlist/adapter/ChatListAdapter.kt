package com.pob.seeat.presentation.view.chat.chatlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pob.seeat.databinding.ItemChatListBinding
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
import com.pob.seeat.presentation.view.chat.chatlist.viewholder.SuccessViewHolder
import com.pob.seeat.presentation.view.chat.chatlist.adapter.ChatListDiffUtil
import com.pob.seeat.data.model.Result

class ChatListAdapter : ListAdapter<Result<ChatListUiItem>, RecyclerView.ViewHolder>(
    ChatListDiffUtil()
) {
    var clickListener: ClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuccessViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is SuccessViewHolder ->
                if(getItem(position) is Result.Success)
                holder.bind((getItem(position) as Result.Success).data as ChatListUiItem, clickListener)
        }
    }

    interface ClickListener {
        fun onClick(item: ChatListUiItem)
    }

    // TODO 성공, 실패, 로딩 등 UiState 추가

}
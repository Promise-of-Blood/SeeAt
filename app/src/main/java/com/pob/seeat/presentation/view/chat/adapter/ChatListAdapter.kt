package com.pob.seeat.presentation.view.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pob.seeat.databinding.ItemChatListBinding
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem

class ChatListAdapter : ListAdapter<UiState<ChatListUiItem>, RecyclerView.ViewHolder>(
    ChatListDiffUtil()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuccessViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is SuccessViewHolder ->
                if(getItem(position) is UiState.Success)
                holder.bind((getItem(position) as UiState.Success).data as ChatListUiItem.ChatItem)
        }
    }

    // TODO 성공, 실패, 로딩 등 UiState 추가

}
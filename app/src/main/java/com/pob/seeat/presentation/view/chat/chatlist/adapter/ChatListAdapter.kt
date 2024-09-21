package com.pob.seeat.presentation.view.chat.chatlist.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.RequestBuilder
import com.pob.seeat.databinding.ItemChatListBinding
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
import com.pob.seeat.presentation.view.chat.chatlist.viewholder.SuccessViewHolder
import com.pob.seeat.data.model.Result
import com.pob.seeat.presentation.common.ViewHolder

enum class ChatListUiState(val type: Int) {
    SUCCESS(1), ERROR(2), LOADING(0)
}

class ChatListAdapter : ListAdapter<Result<ChatListUiItem>, RecyclerView.ViewHolder>(
    ChatListDiffUtil()
) {
    var clickListener: ClickListener? = null
    var setPhotoListener: SetPhotoListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        lateinit var binding : ViewBinding
        lateinit var holder : RecyclerView.ViewHolder
        when(viewType) {
            ChatListUiState.SUCCESS.type -> {
                binding = ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                holder = SuccessViewHolder(binding)
            }
            ChatListUiState.ERROR.type -> {
                // TODO : 오류 창 만들기
            }
            ChatListUiState.LOADING.type -> {
                // TODO : 로딩 창 만들기
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is SuccessViewHolder ->
                if(getItem(position) is Result.Success)
                holder.bind((getItem(position) as Result.Success).data as ChatListUiItem, clickListener, setPhotoListener = setPhotoListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Result.Success -> ChatListUiState.SUCCESS.type
            is Result.Error -> ChatListUiState.ERROR.type
            is Result.Loading -> ChatListUiState.LOADING.type
        }
    }

    interface ClickListener {
        fun onClick(item: ChatListUiItem)
    }

    interface SetPhotoListener {
        fun onSet(photoUrl: String): RequestBuilder<Drawable>
    }

    // TODO 성공, 실패, 로딩 등 UiState 추가

}
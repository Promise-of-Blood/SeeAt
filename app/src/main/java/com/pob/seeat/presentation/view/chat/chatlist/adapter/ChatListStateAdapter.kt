//package com.pob.seeat.presentation.view.chat.chatlist.adapter
//
//import android.view.ViewGroup
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.pob.seeat.presentation.view.chat.chatlist.ChatListUiState
//import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
//
//enum class ChatListUiStateEnum(val type: Int) {
//    SUCCESS(1), ERROR(2), LOADING(0), EMPTY(3)
//}
//
//class ChatListStateAdapter : ListAdapter<ChatListUiState<List<Result<ChatListUiItem>>>, RecyclerView.ViewHolder>(
//    ChatListStateDiffUtil()
//) {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        when(viewType) {
//            ChatListUiStateEnum.SUCCESS.type -> {
//            }
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return when(getItem(position)) {
//            ChatListUiState.Empty -> ChatListUiStateEnum.EMPTY.type
//            is ChatListUiState.Error -> ChatListUiStateEnum.ERROR.type
//            ChatListUiState.Loading -> ChatListUiStateEnum.LOADING.type
//            is ChatListUiState.Success -> ChatListUiStateEnum.SUCCESS.type
//        }
//    }
//
//}
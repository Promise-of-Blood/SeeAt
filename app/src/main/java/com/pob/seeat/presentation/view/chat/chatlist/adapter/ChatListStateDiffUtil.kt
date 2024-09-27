//package com.pob.seeat.presentation.view.chat.chatlist.adapter
//
//import androidx.recyclerview.widget.DiffUtil
//import com.pob.seeat.presentation.view.UiState
//import com.pob.seeat.presentation.view.chat.chatlist.ChatListUiState
//import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
//
//class ChatListStateDiffUtil : DiffUtil.ItemCallback<ChatListUiState<List<Result<ChatListUiItem>>>>() {
//    override fun areItemsTheSame(
//        oldItem: ChatListUiState<List<Result<ChatListUiItem>>>,
//        newItem: ChatListUiState<List<Result<ChatListUiItem>>>,
//    ): Boolean {
//        return when(oldItem) {
//            is ChatListUiState.Success -> {
//                newItem is ChatListUiState.Success
//            }
//
//            is ChatListUiState.Error -> {
//                newItem is ChatListUiState.Error
//            }
//
//            is ChatListUiState.Loading -> {
//                newItem is ChatListUiState.Loading
//            }
//
//            is ChatListUiState.Empty -> {
//                newItem is ChatListUiState.Empty
//            }
//        }
//    }
//
//    override fun areContentsTheSame(
//        oldItem: ChatListUiState<List<Result<ChatListUiItem>>>,
//        newItem: ChatListUiState<List<Result<ChatListUiItem>>>,
//    ): Boolean {
//        return when(oldItem) {
//            is ChatListUiState.Success -> {
//                newItem is ChatListUiState.Success
//            }
//
//            is ChatListUiState.Error -> {
//                newItem is ChatListUiState.Error
//            }
//
//            is ChatListUiState.Loading -> {
//                newItem is ChatListUiState.Loading
//            }
//
//            is ChatListUiState.Empty -> {
//                newItem is ChatListUiState.Empty
//            }
//        }
//    }
//}
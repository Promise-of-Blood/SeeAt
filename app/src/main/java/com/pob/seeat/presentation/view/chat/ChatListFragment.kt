package com.pob.seeat.presentation.view.chat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pob.seeat.databinding.FragmentChatListBinding
import com.pob.seeat.presentation.view.chat.chatlist.adapter.ChatListAdapter
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
import com.pob.seeat.presentation.viewmodel.ChatListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ChatListFragment : Fragment() {
    private val chatListAdapter = ChatListAdapter()
    private val chatListViewModel by viewModels<ChatListViewModel>()

    companion object {
        fun newInstance() = ChatListFragment
    }

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatListViewModel.receiveChatList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvChatList.adapter = chatListAdapter
        Timber.d("onViewCreated Adapters ${chatListViewModel.chatList.value}")
        chatListAdapter.submitList(chatListViewModel.chatList.value)
        viewLifecycleOwner.lifecycleScope.launch {
            chatListViewModel.chatList.collectLatest {
                Timber.d("collectLatestAdapterView $it")
                chatListAdapter.submitList(it)
            }
        }
        chatListAdapter.clickListener = object : ChatListAdapter.ClickListener {
            override fun onClick(item: ChatListUiItem) {
                val intent = Intent(requireContext(), ChattingActivity::class.java)
                intent.putExtra("feedId", item.feedFrom)
                startActivity(intent)
            }
        }
//        chatListAdapter.submitList(ChatListDummyData.getDummyList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
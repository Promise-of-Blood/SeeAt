package com.pob.seeat.presentation.view.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pob.seeat.databinding.FragmentChatListBinding
import com.pob.seeat.presentation.view.chat.adapter.ChatListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatListFragment : Fragment() {
    private val chatListAdapter = ChatListAdapter()

    companion object {
        fun newInstance() = ChattingFragment
    }

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        chatListAdapter.submitList(ChatListDummyData.getDummyList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
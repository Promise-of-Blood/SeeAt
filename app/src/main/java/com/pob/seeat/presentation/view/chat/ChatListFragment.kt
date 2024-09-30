package com.pob.seeat.presentation.view.chat

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.pob.seeat.databinding.FragmentChatListBinding
import com.pob.seeat.presentation.view.chat.chatlist.ChatListUiState
import com.pob.seeat.presentation.view.chat.chatlist.adapter.ChatListAdapter
import com.pob.seeat.presentation.view.chat.items.ChatListUiItem
import com.pob.seeat.presentation.viewmodel.ChatListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import com.pob.seeat.data.model.Result

@AndroidEntryPoint
class ChatListFragment : Fragment() {
    private val chatListAdapter = ChatListAdapter()
    private val chatListViewModel by activityViewModels<ChatListViewModel>()

    companion object {
        fun newInstance() = ChatListFragment
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
        binding.rvChatListSuccess.adapter = chatListAdapter
        Timber.d("onViewCreated Adapters chatList: ${chatListViewModel.chatList}")
        
        // TODO ChatListUiState 속성에 따른 fragment Ui 변경 필요 ->
        (chatListViewModel.chatList.value as ChatListUiState<List<Result<ChatListUiItem>>>).setUiWithState()

        viewLifecycleOwner.lifecycleScope.launch {
            chatListViewModel.receiveChatList()
            Timber.d("viewLifeCycleOwner launch")
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Timber.d("repeat LifeCycleOwner launch")
                launch {
                    chatListViewModel.chatList.collect {
                        Timber.d("collectLatestAdapterView $it")
                        (it as ChatListUiState<List<Result<ChatListUiItem>>>).setUiWithState()
                    }
                }
            }
        }
        
        chatListAdapter.clickListener = object : ChatListAdapter.ClickListener {
            override fun onClick(item: ChatListUiItem) {
                val intent = Intent(requireContext(), ChattingActivity::class.java)
                intent.putExtra("feedId", item.feedFrom)
                intent.putExtra("chatId", item.id)
                intent.putExtra("targetName", item.person)
                startActivity(intent)
            }
        }
        chatListAdapter.setPhotoListener = object : ChatListAdapter.SetPhotoListener {
            override fun onSet(photoUrl: String): RequestBuilder<Drawable> {
                return Glide.with(requireContext()).load(photoUrl)
            }
        }
//        chatListAdapter.submitList(ChatListDummyData.getDummyList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun ChatListUiState<List<Result<ChatListUiItem>>>.setUiWithState() {
        Timber.tag("setUiWithState").d("UiState Setting $this")
        when(this) {
            is ChatListUiState.Success -> {
                Timber.tag("setUiWithState").d("ChatListUiState Success")
                binding.clError.visibility = View.GONE
                binding.clEmpty.visibility = View.GONE
                binding.rvChatListSuccess.visibility = View.VISIBLE
                binding.progressChatListLoading.visibility = View.GONE

                chatListAdapter.submitList((chatListViewModel.chatList.value as ChatListUiState.Success).data.sortedByTime())
            }
            is ChatListUiState.Error -> {
                Timber.tag("setUiWithState").d("ChatListUiState Error")
                binding.clError.visibility = View.VISIBLE
                binding.clEmpty.visibility = View.GONE
                binding.rvChatListSuccess.visibility = View.GONE
                binding.progressChatListLoading.visibility = View.GONE
            }
            is ChatListUiState.Empty -> {
                Timber.tag("setUiWithState").d("ChatListUiState Empty")
                binding.clError.visibility = View.GONE
                binding.clEmpty.visibility = View.VISIBLE
                binding.rvChatListSuccess.visibility = View.GONE
                binding.progressChatListLoading.visibility = View.GONE
            }
            is ChatListUiState.Loading -> {
                Timber.tag("setUiWithState").d("ChatListUiState Loading")
                binding.clError.visibility = View.GONE
                binding.clEmpty.visibility = View.GONE
                binding.rvChatListSuccess.visibility = View.GONE
                binding.progressChatListLoading.visibility = View.VISIBLE
            }
        }
    }

    fun List<Result<ChatListUiItem>>.sortedByTime() : List<Result<ChatListUiItem>> {
        val mutableList = this.toMutableList()
        if(mutableList.filterIsInstance<Result.Success<ChatListUiItem>>().size != mutableList.size) return mutableList
        Timber.tag("sortedByTime Before").d(mutableList.toString())
        mutableList.sortWith(
            compareBy {
                - (it as Result.Success).data.lastTime
            }
        )
        Timber.tag("sortedByTime After").d(mutableList.toString())
        return mutableList
    }
}
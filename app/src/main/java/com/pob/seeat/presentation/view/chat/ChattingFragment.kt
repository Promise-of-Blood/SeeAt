package com.pob.seeat.presentation.view.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.pob.seeat.R
import com.pob.seeat.data.database.chat.ChatRoomDb
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.ActivityChattingBinding
import com.pob.seeat.databinding.FragmentChattingBinding
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.presentation.view.chat.adapter.ChattingAdapter
import com.pob.seeat.presentation.view.home.HomeFragmentDirections
import com.pob.seeat.presentation.viewmodel.ChatViewModel
import com.pob.seeat.presentation.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ChattingFragment : Fragment() {

    private val detailViewModel by viewModels<DetailViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    private val chattingAdapter by lazy { ChattingAdapter() }
    private lateinit var chatRoomDb : ChatRoomDb
    var targetId: String = ""
    var chatId = "none"
    private var targetName = "(알 수 없음)"
    lateinit var feedId : String
//    private val args: ChattingFragmentArgs by navArgs()

    private var _binding : FragmentChattingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        feedId = arguments?.getString("feedId") ?: ""
        chatId = arguments?.getString("chatId") ?: "none"
        targetName = arguments?.getString("targetName") ?: "(알 수 없음)"
        chatRoomDb = ChatRoomDb.getDatabase(requireContext())
        _binding = FragmentChattingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.tag("chatAndFeedID").d("chat: $chatId , feed: $feedId")

        setNotSendWhenEmpty()
        initViewModel()
        getFeedData()
        setSendMessage()

        binding.rvMessage.adapter = chattingAdapter
        binding.rvMessage.itemAnimator = null
        val messageLayoutManager = LinearLayoutManager(requireContext())
        binding.rvMessage.layoutManager = messageLayoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewModel.chatResult.collect {
                    Timber.tag("ChattingAddLog").d("chatResult : $it")

                    chattingAdapter.submitList(chatViewModel.chatResult.value)
//                    chatRoomDb.chatDao().addChatMessage(chatId = chatId, message = it.message, sender = it.sender)
                }
            }
        }
        // TODO 디테일로 가기 (프래그먼트로 바꾸고, 내비게이션 추가해서?)
        binding.clFeedInfo.setOnClickListener {
            val action = ChattingFragmentDirections.actionChattingToDetail(feedId)
            binding.root.findNavController().navigate(action)
        }
    }

    private fun setSendMessage() {

        binding.btnChattingSend.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val inputMessage =  binding.etChattingInput.text.toString()
                val sendCallback : Deferred<Boolean> = async {
                    Timber.tag("ChattingLOG").d("btnChattingSend Clicked : $targetId !")
//                    chatRoomDb.chatDao().addChatMessage(chatId = chatId, message = inputMessage, sender = FirebaseAuth.getInstance().currentUser?.uid ?: "")
                    chatViewModel.sendMessage(
                        feedId = feedId,
                        targetUid = targetId,
                        message = inputMessage,
                        chatId = chatId
                    )
                }
                if(sendCallback.await()) {
                    if(chatId == "none") {
                        getChatIdWhenNone(feedId)
                    }
                }
            }

            binding.etChattingInput.setText("")
        }
    }

    private fun setNotSendWhenEmpty() {
        binding.apply {
            btnChattingSend.isEnabled = false
            etChattingInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    btnChattingSend.isEnabled = !s.isNullOrEmpty()
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }
    }

    private fun getFeedData() {
        detailViewModel.getFeedById(feedId)
    }

    private fun initViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            initAsyncViewModel()
        }
    }

    private suspend fun initAsyncViewModel() {
        Timber.tag("initAsyncViewModel").d("initAsyncViewModel is On")
        detailViewModel.singleFeedResponse.flowWithLifecycle(lifecycle).collectLatest { response ->
            when (response) {
                is Result.Error -> Timber.e("Error: ${response.message}")
                is Result.Loading -> Timber.i("Loading..")
                is Result.Success -> {
                    initFeedData(response.data)
                    initChatViewModel()
                }
            }
        }
    }

    private suspend fun initChatViewModel() = with(chatViewModel) {
        Timber.tag("initChatViewModel").d("initChatViewModel is On")
        if(chatId != "none") {
            initMessage(feedId = feedId, chatId = chatId)
            subscribeMessage(feedId = feedId, chatId = chatId)
            Timber.tag("InitChattingLOG").d("chatResult : ${chatResult.value}")
        }
    }

    private fun initFeedData(feed: FeedModel) = with(binding) {
        println("feed data : $feed")
//        cgMessageFeedTag.addFeedTags(feed.tags)
        toolbarMessage.apply {
            title = targetName
            setNavigationOnClickListener {
//                findNavController(requireActivity(), R.id.fcv).popBackStack()
                requireActivity().finish()
            }
        }
        tvMessageFeedTitle.text = feed.title
        tvMessageFeedContent.text = feed.content
        feed.contentImage.getOrNull(0)?.let {
            Glide.with(requireContext())
                .load(it)
                .into(ivMessageFeed)
        }
        targetId = feed.user?.id.toString()
    }

    private suspend fun getChatIdWhenNone(feedId: String) {
        val getChatId = chatViewModel.getChatId(feedId)
        Timber.tag("whenNoneChattingLOG").d(getChatId)
        if (getChatId != "none") {
            chatId = getChatId
            initChatViewModel()
        }
    }

}


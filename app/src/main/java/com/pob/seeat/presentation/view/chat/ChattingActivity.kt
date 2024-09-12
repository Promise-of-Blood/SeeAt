package com.pob.seeat.presentation.view.chat

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pob.seeat.R
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.ActivityChattingBinding
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.presentation.view.chat.adapter.ChattingAdapter
import com.pob.seeat.presentation.viewmodel.ChatViewModel
import com.pob.seeat.presentation.viewmodel.DetailViewModel
import com.pob.seeat.utils.Utils.px
import com.pob.seeat.utils.Utils.setStatusBarColor
import com.pob.seeat.utils.Utils.toTagList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@AndroidEntryPoint
class ChattingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityChattingBinding.inflate(layoutInflater) }
    private val detailViewModel by viewModels<DetailViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    private val chattingAdapter by lazy { ChattingAdapter() }
    var targetId: String = ""
    var chatId = "none"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        this@ChattingActivity.setStatusBarColor(getColor(R.color.white))

        val feedId = intent.getStringExtra("feedId") ?: ""
        chatId = intent.getStringExtra("chatId") ?: "none"

        initViewModel()
        getFeedData()
//        chatViewModel.subscribeMessage(intent.getStringExtra("feedId") ?: "")
        binding.btnChattingSend.setOnClickListener {
            lifecycleScope.launch {
                val sendCallback : Deferred<Boolean> = async {
                    Timber.tag("ChattingLOG").d("btnChattingSend Clicked : $targetId !")
                    chatViewModel.sendMessage(
                        feedId = feedId,
                        targetUid = targetId,
                        message = binding.etChattingInput.text.toString(),
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

        binding.rvMessage.adapter = chattingAdapter
        binding.rvMessage.itemAnimator = null
        val messageLayoutManager = LinearLayoutManager(this)
        binding.rvMessage.layoutManager = messageLayoutManager

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                chatViewModel.chatResult.collect {
                    Timber.tag("ChattingAddLog").d("chatResult : $it")
                    chattingAdapter.submitList(chatViewModel.chatResult.value)
                }
            }
        }
    }

    private fun getFeedData() {
        val feedId = intent.getStringExtra("feedId") ?: ""
        detailViewModel.getFeedById(feedId)
    }

    private fun initViewModel() {
        lifecycleScope.launch {
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
        val feedId = intent.getStringExtra("feedId") ?: ""
        if(chatId != "none") {
            initMessage(feedId = feedId, chatId = chatId)
            subscribeMessage(feedId = feedId, chatId = chatId)
            Timber.tag("InitChattingLOG").d("chatResult : ${chatResult.value}")
        }
    }

    private fun initFeedData(feed: FeedModel) = with(binding) {
        println("feed data : $feed")
        cgMessageFeedTag.addFeedTags(feed.tags)
        toolbarMessage.apply {
            title = feed.nickname
            setNavigationOnClickListener {
                finish()
            }
        }
        tvMessageFeedTitle.text = feed.title
        tvMessageFeedContent.text = feed.content
        feed.contentImage.getOrNull(0)?.let {
            Glide.with(this@ChattingActivity)
                .load(it)
                .into(ivMessageFeed)
        }
        targetId = feed.user?.id.toString()
    }

    private suspend fun getChatIdWhenNone(feedId: String) {
        val getChatId = chatViewModel.getChatId(feedId)
        Timber.tag("whenNoneChattingLOG").d(getChatId)
        if(getChatId != "none") {
            chatId = getChatId
            initChatViewModel()
        }
    }

    private fun ChipGroup.addFeedTags(tags: List<String>) {
        val containerWidth = this.width
        var totalWidth = 0
        for (tag in tags.toTagList()) {
            val chip = Chip(context).apply {
                text = tag.tagName
                textSize = 12f
                textEndPadding = 4f.px.toFloat()
                setTextColor(AppCompatResources.getColorStateList(context, R.color.black))

                setChipIconResource(tag.tagImage)
                chipBackgroundColor = AppCompatResources.getColorStateList(context, R.color.white)
                chipStrokeWidth = 0f
                chipIconSize = 12f.px.toFloat()
                chipMinHeight = 24f.px.toFloat()
                chipCornerRadius = 32f.px.toFloat()
                iconStartPadding = 4f.px.toFloat()

                elevation = 3f.px.toFloat()
                isCheckable = false
                isClickable = false
            }
            chip.measure(0, 0)
            totalWidth += chip.measuredWidth
            if (totalWidth > containerWidth) break
            this.addView(chip)
        }
    }
}

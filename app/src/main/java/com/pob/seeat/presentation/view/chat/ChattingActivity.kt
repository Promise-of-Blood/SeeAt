package com.pob.seeat.presentation.view.chat

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
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
import com.pob.seeat.utils.Utils.toTagList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ChattingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityChattingBinding.inflate(layoutInflater) }
    private val detailViewModel by viewModels<DetailViewModel>()
    private val chatViewModel by viewModels<ChatViewModel>()
    private val chattingAdapter by lazy { ChattingAdapter() }
    var targetId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val feedId = intent.getStringExtra("feedId") ?: ""

        initViewModel()
        initChatViewModel()
        getFeedData()
//        chatViewModel.subscribeMessage(intent.getStringExtra("feedId") ?: "")
        binding.btnChattingSend.setOnClickListener {
            lifecycleScope.launch {
                Timber.tag("ChattingLOG").d("btnChattingSend Clicked : $targetId !")
                chatViewModel.sendMessage(feedId, targetId, binding.etChattingInput.text.toString())
            }
            binding.etChattingInput.setText("")
        }
        binding.rvMessage.adapter = chattingAdapter
        binding.rvMessage.itemAnimator = null
        val messageLayoutManager = LinearLayoutManager(this)
        binding.rvMessage.layoutManager = messageLayoutManager
        lifecycleScope.launch {
            chatViewModel.chatResult.collect {
                Timber.tag("ChattingAddLog").d("chatResult : $it")
                chattingAdapter.submitList(chatViewModel.chatResult.value)
            }
        }
    }

    private fun getFeedData() {
        val feedId = intent.getStringExtra("feedId") ?: ""
        detailViewModel.getFeedById(feedId)
    }

    private fun initViewModel() = with(detailViewModel) {
        lifecycleScope.launch {
            singleFeedResponse.flowWithLifecycle(lifecycle).collectLatest { response ->
                when (response) {
                    is Result.Error -> Timber.e("Error: ${response.message}")
                    is Result.Loading -> Timber.i("Loading..")
                    is Result.Success -> initFeedData(response.data)
                }
            }
        }
    }

    private fun initChatViewModel() = with(chatViewModel) {
        lifecycleScope.launch {
            initMessage(intent.getStringExtra("feedId") ?: "")
            subscribeMessage(intent.getStringExtra("feedId") ?: "")
            Timber.tag("InitChattingLOG").d("chatResult : ${chatResult.value}")
        }
    }

    private fun initFeedData(feed: FeedModel) = with(binding) {
        println("feed data : $feed")
        cgMessageFeedTag.addFeedTags(feed.tags)
        toolbarMessage.apply {
            title = feed.nickname
            setNavigationOnClickListener {
                setResult(Activity.RESULT_OK)
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

    private fun ChipGroup.addFeedTags(tags: List<String>) {
        val containerWidth = this.width
        var totalWidth = 0
        for (tag in tags.toTagList()) {
            val chip = Chip(context).apply {
                text = tag.tagName
                textSize = 12f
                textEndPadding = 4f.px.toFloat()

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

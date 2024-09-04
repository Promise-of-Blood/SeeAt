package com.pob.seeat.presentation.view.chat

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pob.seeat.R
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.ActivityChattingBinding
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.presentation.viewmodel.DetailViewModel
import com.pob.seeat.utils.Utils.px
import com.pob.seeat.utils.Utils.toTagList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ChattingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityChattingBinding.inflate(layoutInflater) }
    private val detailViewModel by viewModels<DetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViewModel()
        getFeedData()
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
                    is Result.Success -> initView(response.data)
                }
            }
        }
    }

    private fun initView(feed: FeedModel) = with(binding) {
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
        val thumbnail = feed.contentImage.getOrNull(0)
        thumbnail?.let {
            Glide.with(this@ChattingActivity)
                .load(thumbnail)
                .into(ivMessageFeed)
        }
    }

    private fun ChipGroup.addFeedTags(tags: List<String>) {
        for (tag in tags.toTagList()) {
            val chip = Chip(this@ChattingActivity).apply {
                text = tag.tagName
                textSize = 14f
                setChipIconResource(tag.tagImage)

                chipBackgroundColor = getColorStateList(R.color.white)
                chipStrokeWidth = 0f
                chipIconSize = 14f.px.toFloat()
                chipCornerRadius = 32f.px.toFloat()
                chipStartPadding = 10f.px.toFloat()

                elevation = 1f.px.toFloat()

                isCheckable = false
                isClickable = false
            }
            this.addView(chip)
        }
    }
}

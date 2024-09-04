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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.ActivityChattingBinding
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.presentation.view.home.MarginItemDecoration
import com.pob.seeat.presentation.view.home.TagAdapter
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
        rvMessageFeedTag.apply {
            adapter = TagAdapter(feed.tags.toTagList())
            layoutManager =
                LinearLayoutManager(this@ChattingActivity, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(MarginItemDecoration(16f.px))
        }
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
}

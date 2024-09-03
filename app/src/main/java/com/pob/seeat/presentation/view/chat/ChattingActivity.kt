package com.pob.seeat.presentation.view.chat

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.pob.seeat.databinding.ActivityChattingBinding
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.presentation.view.home.MarginItemDecoration
import com.pob.seeat.presentation.view.home.TagAdapter
import com.pob.seeat.utils.Utils.px
import com.pob.seeat.utils.Utils.tagList

class ChattingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityChattingBinding.inflate(layoutInflater) }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initView() = with(binding) {
        val feed = intent.getParcelableExtra("feed", FeedModel::class.java) ?: FeedModel()
        val tags = tagList.shuffled().take(2)

        rvMessageFeedTag.apply {
            adapter = TagAdapter(tags)
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
        Glide.with(this@ChattingActivity)
            .load(feed.location)
            .into(ivMessageFeed)
    }
}
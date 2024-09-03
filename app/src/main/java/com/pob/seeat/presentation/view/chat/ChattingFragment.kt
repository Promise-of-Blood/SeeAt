package com.pob.seeat.presentation.view.chat

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.pob.seeat.databinding.FragmentChattingBinding
import com.pob.seeat.domain.model.FeedModel
import com.pob.seeat.presentation.view.alarm.AlarmFragment
import com.pob.seeat.presentation.view.home.MarginItemDecoration
import com.pob.seeat.presentation.view.home.TagAdapter
import com.pob.seeat.utils.Utils.px
import com.pob.seeat.utils.Utils.tagList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChattingFragment : Fragment() {
    companion object {
        fun newInstance() = ChattingFragment

        @JvmStatic
        fun newInstance(feed: FeedModel) =
            AlarmFragment().apply { arguments = Bundle().apply { putParcelable("feed", feed) } }
    }

    private var _binding: FragmentChattingBinding? = null
    private val binding get() = _binding!!

    private val navArgs: ChattingFragmentArgs by navArgs()
    private var feed: FeedModel? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { feed = it.getParcelable("feed", FeedModel::class.java) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChattingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() = with(binding) {
        val feed = navArgs.feed
        val tags = tagList.shuffled().take(2)

        rvMessageFeedTag.apply {
            adapter = TagAdapter(tags)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(MarginItemDecoration(16f.px))
        }
        toolbarMessage.apply {
            title = feed.nickname
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
        tvMessageFeedTitle.text = feed.title
        tvMessageFeedContent.text = feed.content
        Glide.with(requireContext())
            .load(feed.location)
            .into(ivMessageFeed)
    }
}
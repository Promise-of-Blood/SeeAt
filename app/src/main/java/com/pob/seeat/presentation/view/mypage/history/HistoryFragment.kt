package com.pob.seeat.presentation.view.mypage.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentHistoryBinding
import com.pob.seeat.presentation.common.CustomDecoration
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.mypage.items.HistoryListItem
import com.pob.seeat.presentation.viewmodel.MyPageSharedViewModel
import com.pob.seeat.presentation.viewmodel.UserHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val ARG_POSITION = "position"

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyAdapter by lazy { HistoryAdapter(::onClickListItem) }
    private val userHistoryViewModel by viewModels<UserHistoryViewModel>()
    private val myPageSharedViewModel by activityViewModels<MyPageSharedViewModel>()
    private var position: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { position = it.getInt(ARG_POSITION) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        initSharedViewModel()
        getHistoryList()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView() = with(binding) {
        rvHistory.apply {
            adapter = historyAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            itemAnimator = null
            addItemDecoration(
                CustomDecoration(1f, 48f, resources.getColor(R.color.light_gray, null))
            )
        }
        mbHistoryMore.setOnClickListener {
            val action = HistoryFragmentDirections.actionUserHistoryToUserHistoryList(position ?: 0)
            findNavController().navigate(action)
        }
    }

    private fun initViewModel() = with(userHistoryViewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            history.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest { response ->
                when (response) {
                    is UiState.Error -> {
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    is UiState.Loading -> {
                        binding.rvHistory.visibility = View.INVISIBLE
                        binding.mbHistoryMore.visibility = View.GONE
                    }

                    is UiState.Success -> {
                        historyAdapter.submitList(response.data.take(if (position == 1) 4 else 3))
                        handleEmptyListView(response.data.size)
                    }
                }
            }
        }
    }

    private fun initSharedViewModel() = with(myPageSharedViewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            triggerEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle).collectLatest {
                getHistoryList()
            }
        }
    }

    private fun getHistoryList() = when (position) {
        0 -> userHistoryViewModel.getUserFeedHistory(4)
        1 -> userHistoryViewModel.getUserCommentHistory(5)
        else -> userHistoryViewModel.getUserLikedHistory(4)
    }

    private fun onClickListItem(item: HistoryListItem) {
        val feedId = when (item) {
            is HistoryListItem.FeedItem -> item.feedId
            is HistoryListItem.CommentItem -> item.feedId
        }
        val action = HistoryFragmentDirections.actionUserHistoryToFeedDetail(feedId)
        findNavController().navigate(action)
    }

    private fun handleEmptyListView(size: Int) = with(binding) {
        val defaultString = resources.getString(R.string.empty_default)
        val emptyHistoryStringArray = resources.getStringArray(R.array.empty_history)
        when (size) {
            0 -> {
                binding.rvHistory.visibility = View.GONE
                binding.mbHistoryMore.visibility = View.GONE
                binding.tvHistoryEmpty.visibility = View.VISIBLE
                binding.tvHistoryEmpty.text =
                    position?.let { emptyHistoryStringArray[it] } ?: defaultString
            }

            in 1..4 -> {
                if (position != 1 && size == 4) {
                    binding.rvHistory.visibility = View.VISIBLE
                    binding.mbHistoryMore.visibility = View.VISIBLE
                    binding.tvHistoryEmpty.visibility = View.GONE
                } else {
                    binding.rvHistory.visibility = View.VISIBLE
                    binding.mbHistoryMore.visibility = View.GONE
                    binding.tvHistoryEmpty.visibility = View.GONE
                }
            }

            else -> {
                binding.rvHistory.visibility = View.VISIBLE
                binding.mbHistoryMore.visibility = View.VISIBLE
                binding.tvHistoryEmpty.visibility = View.GONE
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(position: Int) = HistoryFragment().apply {
            arguments = Bundle().apply { putInt(ARG_POSITION, position) }
        }
    }
}
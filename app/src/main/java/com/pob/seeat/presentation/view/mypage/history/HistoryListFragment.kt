package com.pob.seeat.presentation.view.mypage.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.pob.seeat.MainActivity
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentHistoryListBinding
import com.pob.seeat.presentation.common.CustomDecoration
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.mypage.items.HistoryListItem
import com.pob.seeat.presentation.viewmodel.UserHistoryViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryListFragment : Fragment() {
    private var _binding: FragmentHistoryListBinding? = null
    private val binding get() = _binding!!

    private val args: HistoryListFragmentArgs by navArgs()

    private val historyAdapter by lazy { HistoryAdapter(::onClickListItem) }
    private val userHistoryViewModel by activityViewModels<UserHistoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        getHistoryList()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)
        _binding = null
    }

    private fun initView() = with(binding) {
        (activity as MainActivity).setBottomNavigationVisibility(View.GONE)
        tbHistory.apply {
            title = resources.getStringArray(R.array.history_title)[args.position]
            setNavigationIcon(R.drawable.ic_arrow_left)
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
        rvHistory.apply {
            adapter = historyAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                CustomDecoration(
                    1f,
                    48f,
                    resources.getColor(R.color.light_gray, null)
                )
            )
        }
    }

    private fun getHistoryList() = when (args.position) {
        0 -> userHistoryViewModel.getUserFeedHistory()
        1 -> userHistoryViewModel.getUserCommentHistory()
        else -> userHistoryViewModel.getUserLikedHistory()
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
                    }

                    is UiState.Success -> {
                        binding.rvHistory.visibility = View.VISIBLE
                        historyAdapter.submitList(response.data)
                    }
                }
            }
        }
    }

    private fun onClickListItem(item: HistoryListItem) {
        val feedId = when (item) {
            is HistoryListItem.FeedItem -> item.feedId
            is HistoryListItem.CommentItem -> item.feedId
        }
        val action = HistoryListFragmentDirections.actionUserHistoryListToFeedDetail(feedId)
        findNavController().navigate(action)
    }
}
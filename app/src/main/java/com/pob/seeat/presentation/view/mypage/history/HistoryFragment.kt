package com.pob.seeat.presentation.view.mypage.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentHistoryBinding
import com.pob.seeat.presentation.common.CustomDecoration
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.viewmodel.UserHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val ARG_POSITION = "position"

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyAdapter by lazy { HistoryAdapter() }
    private val userHistoryViewModel by viewModels<UserHistoryViewModel>()
    private var position: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        getHistoryList()
    }

    override fun onResume() {
        super.onResume()
        getHistoryList()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView() = with(binding) {
        rvHistory.adapter = historyAdapter
        rvHistory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rvHistory.addItemDecoration(
            CustomDecoration(
                1f, 48f, resources.getColor(R.color.light_gray, null)
            )
        )
        tvHistoryMore.setOnClickListener {
            val action = HistoryFragmentDirections.actionUserHistoryToUserHistoryList(position ?: 0)
            findNavController().navigate(action)
        }
    }

    private fun initViewModel() = with(userHistoryViewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            history.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collectLatest { response ->
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

    private fun getHistoryList() = when (position) {
        0 -> userHistoryViewModel.getUserFeedHistory(3)
        1 -> userHistoryViewModel.getUserCommentHistory(4)
        else -> userHistoryViewModel.getUserLikedHistory(3)
    }

    companion object {

        @JvmStatic
        fun newInstance(position: Int) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_POSITION, position)
                }
            }
    }
}
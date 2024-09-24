package com.pob.seeat.presentation.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pob.seeat.R
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.FragmentReportListBinding
import com.pob.seeat.databinding.LayoutSortOptionBottomSheetBinding
import com.pob.seeat.presentation.common.CustomDecoration
import com.pob.seeat.presentation.view.admin.adapter.AdminRecyclerViewAdapter
import com.pob.seeat.presentation.view.admin.items.AdminListItem
import com.pob.seeat.presentation.viewmodel.AdminReportViewModel
import com.pob.seeat.utils.Utils.px
import com.pob.seeat.utils.dialog.Dialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ReportListFragment : Fragment() {
    private var _binding: FragmentReportListBinding? = null
    private val binding get() = _binding!!

    private val adminReportViewModel by viewModels<AdminReportViewModel>()
    private val adminRecyclerViewAdapter by lazy {
        AdminRecyclerViewAdapter(
            ::onDelete,
            ::onIgnore,
            ::onNavigate,
            ::handleEmptyList,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        initBottomSheet()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView() = with(binding) {
        rvReportList.apply {
            adapter = adminRecyclerViewAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
            addItemDecoration(
                CustomDecoration(
                    1f, 24f, resources.getColor(R.color.light_gray, null)
                )
            )
        }
        cgReportOption.addOptions()
        srReportList.setOnRefreshListener {
            adminReportViewModel.getReportedList()
            srReportList.isRefreshing = false
        }
    }

    private fun initViewModel() = with(adminReportViewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            reportedList.collect { data ->
                when (data) {
                    is Result.Success -> {
                        binding.pbReportList.visibility = View.GONE
                        handleEmptyList(data.data.size)
                        adminRecyclerViewAdapter.submitList(data.data)
                        adminRecyclerViewAdapter.setOriginalList(data.data)
                    }

                    is Result.Error -> {
                        Timber.tag("신고").e(data.message)
                    }

                    is Result.Loading -> {
                        binding.pbReportList.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun initBottomSheet() = with(binding) {
        val bottomSheetView = LayoutSortOptionBottomSheetBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val sortOptions = resources.getStringArray(R.array.admin_sort)

        bottomSheetDialog.setContentView(bottomSheetView.root)
        bottomSheetView.apply {
            listOf(tvOption1, tvOption2).forEachIndexed { index, button ->
                button.text = sortOptions[index]
                button.setOnClickListener {
                    cReportSort.text = sortOptions[index]
                    adminRecyclerViewAdapter.sortByOption(sortOptions[index])
                    bottomSheetDialog.hide()
                }
            }
        }

        cReportSort.setOnClickListener {
            bottomSheetDialog.show()
        }
    }

    private fun handleEmptyList(size: Int) = with(binding) {
        tvReportListEmpty.visibility = if (size == 0) View.VISIBLE else View.GONE
    }

    private fun onDelete(item: AdminListItem) {
        when (item) {
            is AdminListItem.FeedReport -> {
                Dialog.showDialog(
                    requireContext(),
                    "게시글 삭제",
                    getString(R.string.dialog_reported_feed),
                ) {
                    adminReportViewModel.deleteReportedFeed(item.feedId)
                    Toast.makeText(requireContext(), "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }

            is AdminListItem.CommentReport -> {
                Dialog.showDialog(
                    requireContext(),
                    "댓글 삭제",
                    getString(R.string.dialog_reported_comment),
                ) {
                    adminReportViewModel.deleteReportedComment(item.feedId, item.commentId)
                    Toast.makeText(requireContext(), "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            else -> return
        }
    }

    private fun onNavigate(item: AdminListItem) {
        val feedId =
            if (item is AdminListItem.FeedReport) item.feedId else if (item is AdminListItem.CommentReport) item.feedId else ""
        val commentId = if (item is AdminListItem.CommentReport) item.commentId else ""
        if (feedId.isEmpty()) {
            Toast.makeText(requireContext(), "잘못된 접근입니다.", Toast.LENGTH_SHORT).show()
        }
        val action = ReportListFragmentDirections.actionReportListToFeedDetail(feedId, commentId)
        findNavController().navigate(action)
    }

    private fun onIgnore(item: AdminListItem) {
        when (item) {
            is AdminListItem.FeedReport -> {
                Dialog.showDialog(
                    requireContext(),
                    "게시글 신고 무시",
                    getString(R.string.dialog_ignore_reported_feed),
                ) {
                    adminReportViewModel.ignoreReportedFeed(item.feedId)
                    Toast.makeText(requireContext(), "신고를 무시했습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }

            is AdminListItem.CommentReport -> {
                Dialog.showDialog(
                    requireContext(),
                    "댓글 신고 무시",
                    getString(R.string.dialog_ignore_reported_comment),
                ) {
                    adminReportViewModel.ignoreReportedComment(item.commentId)
                    Toast.makeText(requireContext(), "신고를 무시했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            else -> return
        }
    }

    private fun ChipGroup.addOptions() {
        for (option in resources.getStringArray(R.array.admin_option)) {
            val chip = Chip(context).apply {
                text = option
                textSize = 12f
                textEndPadding = 4f.px.toFloat()
                setTextColor(
                    AppCompatResources.getColorStateList(
                        context, R.color.text_option_chip
                    )
                )
                chipBackgroundColor =
                    AppCompatResources.getColorStateList(context, R.color.background_option_chip)
                chipStrokeColor =
                    AppCompatResources.getColorStateList(context, R.color.stroke_option_chip)
                chipStrokeWidth = 1f
                chipCornerRadius = 50f.px.toFloat()
                rippleColor = AppCompatResources.getColorStateList(context, R.color.transparent)
                isCheckable = true
                isClickable = true
            }
            chip.setOnClickListener {
                adminRecyclerViewAdapter.filter.filter(option)
            }
            this.addView(chip)
        }
        this.check(this.getChildAt(1).id)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ReportListFragment()
    }
}
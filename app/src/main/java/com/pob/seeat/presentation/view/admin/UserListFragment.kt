package com.pob.seeat.presentation.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pob.seeat.R
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.FragmentUserListBinding
import com.pob.seeat.databinding.LayoutProfileBottomSheetBinding
import com.pob.seeat.domain.model.UserInfoModel
import com.pob.seeat.presentation.common.CustomDecoration
import com.pob.seeat.presentation.view.UiState
import com.pob.seeat.presentation.view.admin.adapter.AdminRecyclerViewAdapter
import com.pob.seeat.presentation.view.admin.adapter.AdminReportRecyclerViewAdapter
import com.pob.seeat.presentation.view.admin.adapter.Searchable
import com.pob.seeat.presentation.view.admin.items.AdminListItem
import com.pob.seeat.presentation.view.admin.items.AdminSearchTypeEnum
import com.pob.seeat.presentation.viewmodel.AdminUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

private const val ADMIN_USER_TAG = "관리자 유저 목록"

@AndroidEntryPoint
class UserListFragment : Fragment(), Searchable {
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private val adminUserViewModel: AdminUserViewModel by viewModels()
    private val adminRecyclerViewAdapter by lazy { AdminRecyclerViewAdapter(onClick = ::onClickListItem) }

    private var _bottomSheetBinding: LayoutProfileBottomSheetBinding? = null
    private val bottomSheetBinding get() = _bottomSheetBinding!!

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val bottomSheetRecyclerViewAdapter by lazy {
        AdminReportRecyclerViewAdapter(
            handleEmptyList = ::handleEmptyList
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModels()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _bottomSheetBinding = null
    }

    override fun performSearch(type: AdminSearchTypeEnum, query: CharSequence?) {
        adminRecyclerViewAdapter.performSearch(type, query)
    }

    private fun initView() = with(binding) {
        rvUserList.apply {
            adapter = adminRecyclerViewAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
            addItemDecoration(
                CustomDecoration(
                    1f, 24f, resources.getColor(R.color.light_gray, null)
                )
            )
        }
        srUserList.setOnRefreshListener {
            adminUserViewModel.getUserList()
            srUserList.isRefreshing = false
        }
    }

    private fun initViewModels() {
        initUserListViewModel()
        initUserDetailViewModel()
        initReportedListViewModel()
    }

    private fun initUserListViewModel() = with(adminUserViewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            userList.collect { data ->
                when (data) {
                    is Result.Success -> {
                        binding.pbUserList.visibility = View.GONE
                        adminRecyclerViewAdapter.submitList(data.data)
                        adminRecyclerViewAdapter.setOriginalList(data.data)
                    }

                    is Result.Error -> Timber.tag(ADMIN_USER_TAG).e(data.message)
                    is Result.Loading -> binding.pbUserList.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initUserDetailViewModel() = with(adminUserViewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            userDetail.collect { data ->
                when (data) {
                    is UiState.Success -> {
                        binding.pbUserList.visibility = View.GONE
                        showBottomSheet(data.data)
                    }

                    is UiState.Error -> Timber.tag(ADMIN_USER_TAG).e(data.message)
                    is UiState.Loading -> binding.pbUserList.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initReportedListViewModel() = with(adminUserViewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            reportedList.collect { data ->
                when (data) {
                    is Result.Success -> {
                        handleEmptyList(data.data.size)
                        bottomSheetRecyclerViewAdapter.submitList(data.data)
                        bottomSheetRecyclerViewAdapter.setOriginalList(data.data)
                    }

                    is Result.Error -> Timber.tag(ADMIN_USER_TAG).e(data.message)
                    is Result.Loading -> Timber.tag(ADMIN_USER_TAG).i("로딩중...")
                }
            }
        }
    }

    private fun onClickListItem(item: AdminListItem) {
        (item as? AdminListItem.User)?.let {
            adminUserViewModel.getUserDetail(item.uid)
            adminUserViewModel.getReportedList(item.uid)
        }
    }

    private fun handleEmptyList(size: Int) {
        if (_bottomSheetBinding == null) initBottomSheet()
        bottomSheetBinding.tvProfileEmpty.visibility = if (size > 0) View.GONE else View.VISIBLE
    }

    private fun showBottomSheet(user: UserInfoModel) {
        if (_bottomSheetBinding == null) initBottomSheet()
        bottomSheetBinding.apply {
            // 사용자 정보
            tvProfileNickname.text = user.nickname
            tvProfileEmail.text = user.email
            tvProfileIntroduce.text = user.introduce
            ivProfileBadge.visibility = if (user.isAdmin) View.VISIBLE else View.GONE
            Glide.with(requireContext())
                .load(user.profileUrl.ifBlank { R.drawable.baseline_person_24 }).circleCrop()
                .into(ivProfileImage)
            llProfileReportCount.visibility =
                if (user.reportedCount > 0) View.VISIBLE else View.GONE
            tvProfileReportCount.text = getString(R.string.admin_report_count, user.reportedCount)

            // 사용자 관리
            sbProfileToggleAdmin.isChecked = user.isAdmin
            sbProfileToggleAdmin.setOnCheckedChangeListener { _, isChecked ->
                ivProfileBadge.visibility = if (isChecked) View.VISIBLE else View.GONE
            }
            acbProfileDeleteUser.setOnClickListener { adminUserViewModel.deleteUser(user.uid) }

            // 사용자 신고 기록
            rvProfileReport.adapter = bottomSheetRecyclerViewAdapter
            rvProfileReport.layoutManager = LinearLayoutManager(requireContext())
            rvProfileReport.itemAnimator = null
        }

        // 관리자 권한 업데이트
        bottomSheetDialog.setOnDismissListener {
            val isAdminChecked = bottomSheetBinding.sbProfileToggleAdmin.isChecked
            if (user.isAdmin != isAdminChecked) {
                adminUserViewModel.updateIsAdmin(user.uid, isAdminChecked)
            }
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun initBottomSheet() {
        _bottomSheetBinding = LayoutProfileBottomSheetBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        // 스크롤 설정
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
        bottomSheetBinding.rvProfileReport.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 리사이클러뷰 목록의 최상단일 때 bottomSheet 드래그 가능
                bottomSheetBehavior.isDraggable = !recyclerView.canScrollVertically(-1)
            }
        })
        bottomSheetBinding.ablProfileLayout.addOnOffsetChangedListener { _, verticalOffset ->
            // collapse 상태가 아닐 때만 bottomSheet 드래그 가능
            bottomSheetBehavior.isDraggable = verticalOffset == 0
        }

        // 리사이클러뷰 설정
        bottomSheetBinding.rvProfileReport.apply {
            adapter = bottomSheetRecyclerViewAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
            addItemDecoration(
                CustomDecoration(
                    1f, 24f, resources.getColor(R.color.light_gray, null)
                )
            )
        }
        bottomSheetBinding.rgProfileReport.setOnCheckedChangeListener { _, checked ->
            when (checked) {
                R.id.rb_profile_feed -> bottomSheetRecyclerViewAdapter.filter.filter("게시글")
                R.id.rb_profile_comment -> bottomSheetRecyclerViewAdapter.filter.filter("댓글")
                else -> bottomSheetRecyclerViewAdapter.filter.filter("전체")
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserListFragment()
    }
}
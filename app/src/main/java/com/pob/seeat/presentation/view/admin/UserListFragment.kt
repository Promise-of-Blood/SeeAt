package com.pob.seeat.presentation.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pob.seeat.R
import com.pob.seeat.data.model.Result
import com.pob.seeat.databinding.FragmentUserListBinding
import com.pob.seeat.presentation.common.CustomDecoration
import com.pob.seeat.presentation.view.admin.adapter.AdminRecyclerViewAdapter
import com.pob.seeat.presentation.view.admin.items.AdminListItem
import com.pob.seeat.presentation.viewmodel.AdminUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class UserListFragment : Fragment() {
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private val adminRecyclerViewAdapter by lazy { AdminRecyclerViewAdapter(::onClickListItem) }
    private val adminUserViewModel: AdminUserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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

    private fun initViewModel() = with(adminUserViewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            userList.collect { data ->
                when (data) {
                    is Result.Success -> {
                        binding.pbUserList.visibility = View.GONE
                        adminRecyclerViewAdapter.submitList(data.data)
                    }

                    is Result.Error -> {
                        Timber.tag("유저 목록").e(data.message)
                    }

                    is Result.Loading -> {
                        binding.pbUserList.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun onClickListItem(item: AdminListItem) {
        // TODO: 상세 페이지로 이동
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserListFragment()
    }
}
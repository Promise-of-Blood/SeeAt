package com.pob.seeat.presentation.view.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentAdminBinding
import com.pob.seeat.presentation.view.admin.adapter.AdminViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView() = with(binding) {
        (activity as AdminActivity).setNavigateButton(false)
        vpAdmin.adapter = AdminViewPagerAdapter(requireActivity())
        TabLayoutMediator(tlAdmin, vpAdmin) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.admin_tab_user)
                1 -> tab.text = getString(R.string.admin_tab_report)
            }
        }.attach()
    }
}
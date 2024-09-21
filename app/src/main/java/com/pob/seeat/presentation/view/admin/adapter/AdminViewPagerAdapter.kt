package com.pob.seeat.presentation.view.admin.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pob.seeat.presentation.view.admin.ReportListFragment
import com.pob.seeat.presentation.view.admin.UserListFragment

class AdminViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val fragments = listOf(UserListFragment.newInstance(), ReportListFragment.newInstance())
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int) = fragments[position]
}
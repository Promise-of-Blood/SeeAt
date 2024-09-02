package com.pob.seeat.presentation.view.mypage

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pob.seeat.presentation.view.mypage.history.HistoryFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int) = HistoryFragment.newInstance(position)
}
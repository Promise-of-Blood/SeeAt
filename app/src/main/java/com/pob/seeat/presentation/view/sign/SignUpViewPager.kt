package com.pob.seeat.presentation.view.sign

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class SignUpViewPager(activity : FragmentActivity):FragmentStateAdapter(activity) {
    val fragments : List<Fragment> = listOf(
        SignUpNameFragment(),
        SignUpPhotoFragment(),
        SignUpIntroduceFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
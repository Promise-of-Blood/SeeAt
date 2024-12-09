package com.pob.seeat.presentation.view.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentAdminBinding
import com.pob.seeat.presentation.view.admin.adapter.AdminViewPagerAdapter
import com.pob.seeat.presentation.view.admin.adapter.Searchable
import com.pob.seeat.presentation.view.admin.items.AdminSearchTypeEnum
import com.pob.seeat.utils.ToggleLayoutAnimation
import com.pob.seeat.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminFragment : Fragment() {
    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    override fun onPause() {
        super.onPause()
        binding.etAdminSearch.text?.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initView() = with(binding) {
        (activity as AdminActivity).setNavigateButton(false)
        val viewPagerAdapter = AdminViewPagerAdapter(requireActivity())
        vpAdmin.adapter = viewPagerAdapter
        TabLayoutMediator(tlAdmin, vpAdmin) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.admin_tab_user)
                1 -> tab.text = getString(R.string.admin_tab_report)
            }
        }.attach()

        // 검색
        ivAdminSearchClear.setOnClickListener { etAdminSearch.text?.clear() }
        (activity as AdminActivity).onClickSearchButton {
            if (llAdminSearch.visibility == View.GONE) {
                ToggleLayoutAnimation.expand(llAdminSearch)
                Utils.showKeyboard(etAdminSearch)
            } else {
                ToggleLayoutAnimation.collapse(llAdminSearch)
                Utils.hideKeyboard(etAdminSearch)
                etAdminSearch.text?.clear()
            }
        }
        etAdminSearch.setOnEditorActionListener { textView, _, _ ->
            viewPagerAdapter.getFragment(vpAdmin.currentItem).let {
                (it as Searchable).performSearch(AdminSearchTypeEnum.CONTENT, textView.text)
            }
            true
        }
        etAdminSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                ivAdminSearchClear.visibility = if (p0.isNullOrEmpty()) View.GONE else View.VISIBLE
                viewPagerAdapter.getFragment(vpAdmin.currentItem).let {
                    (it as Searchable).performSearch(AdminSearchTypeEnum.CONTENT, p0)
                }
            }
        })
        vpAdmin.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewPagerAdapter.getFragment(position).let {
                    if (it.isAdded && it is Searchable) it.performSearch(
                        AdminSearchTypeEnum.CONTENT,
                        etAdminSearch.text
                    )
                }
            }
        })
    }
}
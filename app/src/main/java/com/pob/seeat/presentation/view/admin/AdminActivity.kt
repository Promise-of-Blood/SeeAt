package com.pob.seeat.presentation.view.admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.pob.seeat.R
import com.pob.seeat.databinding.ActivityAdminBinding
import com.pob.seeat.presentation.view.admin.adapter.AdminViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAdminBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    private fun initView() = with(binding) {
        vpAdmin.adapter = AdminViewPagerAdapter(this@AdminActivity)
        TabLayoutMediator(tlAdmin, vpAdmin) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.admin_tab_user)
                1 -> tab.text = getString(R.string.admin_tab_report)
            }
        }.attach()

        setSupportActionBar(tbAdmin)
        ivAdminClose.setOnClickListener { finish() }
    }
}
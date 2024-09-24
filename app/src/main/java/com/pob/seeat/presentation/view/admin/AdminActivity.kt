package com.pob.seeat.presentation.view.admin

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.pob.seeat.R
import com.pob.seeat.databinding.ActivityAdminBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAdminBinding.inflate(layoutInflater) }
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navController = getNavController() ?: return
                if (navController.currentDestination?.id == R.id.admin) finish()
                else navController.navigateUp()
            }
        }

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
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }


    private fun initView() = with(binding) {
        setSupportActionBar(tbAdmin)
        ivAdminClose.setOnClickListener { finish() }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcv_admin) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            ivAdminTitle.text = destination.label
        }
        ivAdminLogo.setOnClickListener { navController.navigate(R.id.admin) }
    }

    fun setNavigateButton(isBackButton: Boolean = true) = with(binding) {
        ivAdminSearch.visibility = if (isBackButton) View.GONE else View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(isBackButton)
        tbAdmin.setNavigationOnClickListener {
            getNavController()?.navigateUp()
        }
    }

    private fun getNavController() = try {
        binding.fcvAdmin.findNavController()
    } catch (e: Exception) {
        null
    }

    fun onClickSearchButton(onClick: () -> Unit) = with(binding) {
        ivAdminSearch.setOnClickListener { onClick() }
    }
}
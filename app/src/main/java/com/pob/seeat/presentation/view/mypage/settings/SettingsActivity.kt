package com.pob.seeat.presentation.view.mypage.settings

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pob.seeat.R
import com.pob.seeat.databinding.ActivitySettingsBinding
import com.pob.seeat.utils.GoogleAuthUtil
import com.pob.seeat.utils.GoogleAuthUtil.getUserEmail

class SettingsActivity : AppCompatActivity() {

    private val binding : ActivitySettingsBinding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()


    }

    private fun initView() = with(binding){

        tvUserEmail.text = getUserEmail()

        settingsLogout.setOnClickListener {
            GoogleAuthUtil.googleLogout(this@SettingsActivity)
        }

        settingsDeleteAccount.setOnClickListener {
            GoogleAuthUtil.googleWithdrawal(this@SettingsActivity)
        }
    }
}
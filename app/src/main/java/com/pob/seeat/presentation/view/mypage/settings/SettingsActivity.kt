package com.pob.seeat.presentation.view.mypage.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pob.seeat.R
import com.pob.seeat.databinding.ActivitySettingsBinding
import com.pob.seeat.utils.GoogleAuthUtil
import com.pob.seeat.utils.GoogleAuthUtil.getUserEmail
import com.pob.seeat.utils.GoogleAuthUtil.getUserUid
import timber.log.Timber

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
        GoogleAuthUtil.initialize(this@SettingsActivity)
        tvUserEmail.text = getUserEmail()

        settingsLogout.setOnClickListener {
            GoogleAuthUtil.googleLogout(this@SettingsActivity)
        }

        settingsDeleteAccount.setOnClickListener {
            GoogleAuthUtil.googleWithdrawal(this@SettingsActivity)
        }
    }

}
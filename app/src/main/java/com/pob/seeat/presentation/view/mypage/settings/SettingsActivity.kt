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
import com.pob.seeat.R
import com.pob.seeat.databinding.ActivitySettingsBinding
import com.pob.seeat.presentation.view.sign.LoginActivity
import com.pob.seeat.utils.GoogleAuthUtil
import com.pob.seeat.utils.GoogleAuthUtil.getUserEmail
import com.pob.seeat.utils.GoogleAuthUtil.reAuthentification

class SettingsActivity : AppCompatActivity() {

    private val binding: ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(
            layoutInflater
        )
    }

    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

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

    private fun initView() = with(binding) {
        GoogleAuthUtil.initialize(this@SettingsActivity)

        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                GoogleAuthUtil.handleReauthResult(
                    result.data,
                    onSuccess = {
                        Log.d("SettingsActivity", "Re-authentication successful")
                        GoogleAuthUtil.googleWithdrawal(this@SettingsActivity) // 재인증 성공 후 회원탈퇴 호출
                    },
                    onFailure = {
                        Toast.makeText(this@SettingsActivity, "재인증에 실패했습니다", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            } else {
                Toast.makeText(this@SettingsActivity, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        tvUserEmail.text = getUserEmail()

        settingsLogout.setOnClickListener {
            GoogleAuthUtil.googleLogout(this@SettingsActivity)
        }

        settingsDeleteAccount.setOnClickListener {
            reAuthentification(this@SettingsActivity, googleSignInLauncher)
        }

        tbSetting.setNavigationOnClickListener { finish() }
    }


    private fun navigateToLoginScreen() {
        Toast.makeText(this, "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
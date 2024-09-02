package com.pob.seeat.presentation.view.sign

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pob.seeat.R
import com.pob.seeat.databinding.ActivitySignUpBinding
import com.pob.seeat.presentation.viewmodel.UserInfoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    val signUpBinding : ActivitySignUpBinding by lazy { ActivitySignUpBinding.inflate(layoutInflater) }

    private val userViewModel : UserInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(signUpBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
    }

    private fun initView() = with(signUpBinding){

        val uid = intent.getStringExtra("uid") ?: ""
        val email = intent.getStringExtra("email") ?:""
        val nickname = intent.getStringExtra("nickname") ?:""

        userViewModel.saveTempUserInfo(uid = uid, email = email, name= nickname)
        Log.d("TempUserInfo","tempUserInfo : ${userViewModel.tempUserInfo.value}")

        val vpAdapter = SignUpViewPager(this@SignUpActivity)

        vpSignUp.apply {
            adapter = vpAdapter
            isUserInputEnabled = false
        }

    }
}
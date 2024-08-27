package com.pob.seeat.presentation.view

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.pob.seeat.BuildConfig
import com.pob.seeat.R
import com.pob.seeat.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private val binding : ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    private var email : String = ""
    private var gender : String = ""
    private var name : String = ""


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
        btnNaverLogin.setOnClickListener {
            naverLogin()
        }


    }

    private fun naverLogin(){
        var naverToken : String? = ""

        NaverIdLoginSDK.initialize(this,BuildConfig.NAVER_CLIENT_ID,BuildConfig.NAVER_CLIENT_SECRET,BuildConfig.NAVER_CLIENT_NAME)


        val oAuthLoginCallback = object :OAuthLoginCallback{
            override fun onSuccess() {
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse>{
                    override fun onSuccess(result: NidProfileResponse) {
                        name = result.profile?.name.toString()
                        email = result.profile?.email.toString()
                        gender = result.profile?.gender.toString()

                        Log.e("네이버로그인  성공", "이름 : $name , 이메일 : $email , 성별 $gender")
                    }

                    override fun onError(errorCode: Int, message: String) {
                        TODO("Not yet implemented")
                    }

                    override fun onFailure(httpStatus: Int, message: String) {
                        TODO("Not yet implemented")
                    }
                })
            }

            override fun onFailure(httpStatus: Int, message: String) {
                TODO("Not yet implemented")
            }

            override fun onError(errorCode: Int, message: String) {
                val naverAccessToken = NaverIdLoginSDK.getAccessToken()
                Log.e("네이버로그인 에러", "naverAccessToken : $naverAccessToken")
            }
        }


        NaverIdLoginSDK.authenticate(this,oAuthLoginCallback)
    }
}
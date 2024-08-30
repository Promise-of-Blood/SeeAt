package com.pob.seeat.presentation.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.auth.User
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AppsErrorCause
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.pob.seeat.BuildConfig
import com.pob.seeat.MainActivity
import com.pob.seeat.R
import com.pob.seeat.databinding.ActivityLoginBinding
import com.pob.seeat.utils.GoogleAuthUtil
import com.pob.seeat.utils.GoogleAuthUtil.firebaseAuthWithGoogle
import com.pob.seeat.utils.GoogleAuthUtil.googleLogin


class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            GoogleAuthUtil.handleSignInResult(this, result.resultCode, result.data,
                onSuccess = {
                    Toast.makeText(this, "환영합니다!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                },
                onFailure = {
                    Toast.makeText(this, "Google 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                })
        }

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

        GoogleAuthUtil.initialize(this@LoginActivity)

        loginCheck()


        clBtnLogin.setOnClickListener {
            GoogleAuthUtil.googleLogin(this@LoginActivity,googleSignInLauncher)
        }


    }

    private fun loginCheck(){
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null){
            Log.d("LoginActivity","현재 로그인 유저 : ${currentUser.email}")
            navigateToHome()
        }
    }

    private fun navigateToHome(){
        val intent = Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

//    서버 필요해서 일단 죽임
//    private fun kakaoLogin() {
//
//        //로그인 callBack
//        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//            if (error != null) {
//                when {
//                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
//                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
//                    }
//
//                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
//                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
//                    }
//
//                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
//                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//
//                    error.toString() == AppsErrorCause.InvalidRequest.toString() -> {
//                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
//                    }
//
//                    error.toString() == AppsErrorCause.InvalidScope.toString() -> {
//                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
//                    }
//
//                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
//                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//
//                    error.toString() == AuthErrorCause.ServerError.toString() -> {
//                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
//                    }
//
//                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
//                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
//                    }
//
//                    else -> { // Unknown
//                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } else if (token != null) {
//                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
////                val intent = Intent(this, SecondActivity::class.java)
////                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
////                finish()
//            }
//        }
//
//        // 로그인 정보 확인
//        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
//            if (error != null) {
//                Toast.makeText(this, "토큰 실패", Toast.LENGTH_SHORT).show()
//            } else if (tokenInfo != null) {
//                Toast.makeText(this, "토큰 성공", Toast.LENGTH_SHORT).show()
////                val intent = Intent(this, SecondActivity::class.java)
////                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
////                finish()
//            }
//        }
//
//
//        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
//            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
//        } else {
//            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
//        }
//
//    }
//
//
//    private fun naverLogin() {
//        var naverToken: String? = ""
//
//        NaverIdLoginSDK.initialize(
//            this,
//            BuildConfig.NAVER_CLIENT_ID,
//            BuildConfig.NAVER_CLIENT_SECRET,
//            BuildConfig.NAVER_CLIENT_NAME
//        )
//
//
//        val oAuthLoginCallback = object : OAuthLoginCallback {
//            override fun onSuccess() {
//
//                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
//
//                    override fun onSuccess(result: NidProfileResponse) {
//                        val name = result.profile?.name.toString()
//                        val email = result.profile?.email.toString()
//                        val gender = result.profile?.gender.toString()
//
//                        Log.e("네이버로그인  성공", "이름 : $name , 이메일 : $email , 성별 $gender")
//                    }
//
//                    override fun onError(errorCode: Int, message: String) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onFailure(httpStatus: Int, message: String) {
//                        TODO("Not yet implemented")
//                    }
//                })
//            }
//
//            override fun onFailure(httpStatus: Int, message: String) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onError(errorCode: Int, message: String) {
//                val naverAccessToken = NaverIdLoginSDK.getAccessToken()
//                Log.e("네이버로그인 에러", "naverAccessToken : $naverAccessToken")
//            }
//        }
//        NaverIdLoginSDK.authenticate(this, oAuthLoginCallback)
//    }
}
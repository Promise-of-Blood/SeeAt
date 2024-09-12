package com.pob.seeat.presentation.view.sign

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pob.seeat.MainActivity
import com.pob.seeat.R
import com.pob.seeat.databinding.ActivityLoginBinding
import com.pob.seeat.presentation.view.PermissionGuideActivity
import com.pob.seeat.presentation.viewmodel.UserInfoViewModel
import com.pob.seeat.utils.GoogleAuthUtil
import com.pob.seeat.utils.NotificationTokenUtils.initNotificationToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    private val userViewModel: UserInfoViewModel by viewModels()

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            GoogleAuthUtil.handleSignInResult(this, result.resultCode, result.data,
                onSuccess = { uid, email, nickname ->
                        isOurFamily(
                            email,
                            {
                                hideProgressBar()
                                navigateToHome()
                            },
                            {
                                hideProgressBar()
                                navigateToSignUp(uid, email, nickname)
                            }
                        )
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
        initNotificationToken()
    }


    private fun initView() = with(binding) {

        val sharedPref = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val isFirstRun = sharedPref.getBoolean("isFirstRun",true)

        if(isFirstRun){
            val intent = Intent(this@LoginActivity,PermissionGuideActivity::class.java)
            startActivity(intent)

            val editor = sharedPref.edit()
            editor.putBoolean("isFirstRun",false)
            editor.apply()
        }

        GoogleAuthUtil.initialize(this@LoginActivity)

        loginCheck()


        clBtnLogin.setOnClickListener {
            showProgressBar()
            GoogleAuthUtil.googleLogin(this@LoginActivity, googleSignInLauncher)
        }


    }

    private fun loginCheck() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        if (currentUser != null && uid != null) {
            userViewModel.getUserInfo(uid)

            // 비동기로 StateFlow를 감지하여 홈 화면으로 이동
            lifecycleScope.launch {
                userViewModel.userInfo.collect { userInfo ->
                    if (userInfo != null) {
                        hideProgressBar()
                        navigateToHome()
                    }
                }
            }
        }
    }

    private fun showProgressBar(){
        binding.pbLogin.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        binding.pbLogin.visibility = View.GONE
    }


    private fun navigateToHome() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main){
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun navigateToSignUp(uid: String, email: String, nickname: String) {
        userViewModel.signUp(uid,email,nickname, profileUrl ="" , introduce ="",token="")
        lifecycleScope.launch {
            withContext(Dispatchers.Main){
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java).apply {
                    putExtra("uid", uid)
                    putExtra("email", email)
                    putExtra("nickname", nickname)
                }
                startActivity(intent)
            }
        }


    }

    private fun isOurFamily(email: String, onUserExists: () -> Unit, onUserNotExist: () -> Unit) {
        val database = FirebaseFirestore.getInstance()
        Log.d("가족","$email")
        if (email.isNotEmpty()) {
            // 이메일 필드가 email과 일치하는 문서 찾기
            database.collection("user")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        onUserNotExist() // 일치하는 문서가 없는 경우
                    } else {
                        onUserExists()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("가족", "가족확인 에러, $exception")
                }
        } else {
            onUserNotExist()
        }
    }


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

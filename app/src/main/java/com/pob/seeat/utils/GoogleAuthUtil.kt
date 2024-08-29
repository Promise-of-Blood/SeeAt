package com.pob.seeat.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pob.seeat.BuildConfig
import com.pob.seeat.MainActivity
import com.pob.seeat.presentation.view.LoginActivity

object GoogleAuthUtil {
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var firebaseAuth : FirebaseAuth
    private const val RC_SIGN_IN = 1001

    //초기화
    fun initialize(activity : Activity){

        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.WEB_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
        Log.d("GoogleAuthUtil", "GoogleSignInClient initialized")
    }

    //로그인
    fun googleLogin(activity: Activity,launcher : ActivityResultLauncher<Intent>) {
        try {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
            Log.d("GoogleAuthUtil", "Google sign-in intent launched")
        } catch (e: Exception) {
            Log.e("GoogleAuthUtil", "Error launching Google sign-in: ${e.message}", e)
        }
    }

    fun handleSignInResult(activity: Activity, requestCode: Int, data: Intent?, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!, activity, onSuccess, onFailure)
        } catch (e: ApiException) {
            Log.e("GoogleAuthUtil", "Login failed: ${e.message}", e)
            onFailure()
        }
    }

    //파이어베이스 인증
    fun firebaseAuthWithGoogle(idToken: String, activity: Activity, onSuccess: () -> Unit, onFailure: () -> Unit){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    Log.e("GoogleAuthUtil", "Firebase authentication failed: ${task.exception?.message}", task.exception)
                    onFailure()
                }
            }

    }


    //로그아웃
    fun googleLogout(activity: Activity){
        // Firebase 로그아웃
        firebaseAuth.signOut()

        // 구글 로그아웃
        googleSignInClient.signOut().addOnCompleteListener(activity) {
            if(FirebaseAuth.getInstance().currentUser == null){
                Toast.makeText(activity, "구글 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity,LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity.startActivity(intent)
                activity.finish()
            }else{
                Toast.makeText(activity, "로그아웃 실패", Toast.LENGTH_SHORT).show()
            }


        }

    }


    //회원 탈퇴
    fun googleWithdrawal(activity: Activity) {
        firebaseAuth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                googleSignInClient.revokeAccess().addOnCompleteListener(activity) {
                    Toast.makeText(activity, "구글 계정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity,LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity.startActivity(intent)
                    activity.finish()

                }
            } else {
                Toast.makeText(activity, "계정 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //로그 찍어보는 용
//    fun checkCurrentUser(activity: Activity){
//        val currentUser = firebaseAuth.currentUser?.uid ?: "없음"
//        Log.d("현재유저","${currentUser}")
//
//    }

}
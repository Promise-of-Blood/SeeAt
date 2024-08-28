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

object GoogleAuthUtil {
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var firebaseAuth : FirebaseAuth
    private const val RC_SIGN_IN = 1001

    fun initialize(activity : Activity){
        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.WEB_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
        Log.d("GoogleAuthUtil", "GoogleSignInClient initialized")
    }

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

    fun googleLogout(activity: Activity){
        // Firebase 로그아웃
        firebaseAuth.signOut()

        // 구글 로그아웃
        googleSignInClient.signOut().addOnCompleteListener(activity) {
            Toast.makeText(activity, "구글 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun googleWithdrawal(activity: Activity){
        firebaseAuth.currentUser?.delete()?.addOnCompleteListener { task ->
            if(task.isSuccessful){
                googleSignInClient.revokeAccess().addOnCompleteListener(activity){
                    Toast.makeText(activity, "구글 계정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(activity,"계정 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
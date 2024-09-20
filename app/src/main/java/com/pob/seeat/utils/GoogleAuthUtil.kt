package com.pob.seeat.utils

import android.app.Activity
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pob.seeat.BuildConfig
import com.pob.seeat.presentation.view.sign.LoginActivity
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

    fun getUserEmail():String{
        return firebaseAuth.currentUser?.email ?: ""
    }

    fun getUserUid():String?{
        val uid = firebaseAuth.currentUser?.uid

        if(uid!= null){
            Log.d("UID확인","현재 로그인 된 사용자 UID : $uid")
        }else{
            Log.d("UID확인","uid 못가져옴")
        }
        return uid
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

    fun handleSignInResult(activity: Activity, requestCode: Int, data: Intent?, onSuccess: (uid:String, email:String, nickname:String) -> Unit, onFailure: () -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!, activity, { uid, email, displayName ->
                onSuccess(uid, email, displayName)  // 성공 시 UID, 이메일, 닉네임 전달
            }, onFailure)
        } catch (e: ApiException) {
            Log.e("GoogleAuthUtil", "Login failed: ${e.message}", e)
            onFailure()
        }
    }

    //파이어베이스 인증
    fun firebaseAuthWithGoogle(idToken: String, activity: Activity, onSuccess: (uid:String, email:String, nickname:String) -> Unit, onFailure: () -> Unit){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val uid = user?.uid ?: ""
                    val email = user?.email ?: ""
                    val nickname = user?.displayName ?: ""

                    if(uid.isNotEmpty()){
                        onSuccess(uid,email,nickname)
                    }else{
                        onFailure()
                    }
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
                val intent = Intent(activity, LoginActivity::class.java)
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
        val user = firebaseAuth.currentUser
        val uid = user?.uid // UID를 계정 삭제 전에 저장합니다.

        if (user != null && uid != null) {
            user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("GoogleAuthUtil", "User account deleted from Auth")
                    googleSignInClient.revokeAccess().addOnCompleteListener {
                        if (it.isSuccessful) {
                            deleteProfileImage(uid){imageDeleteSuccess ->
                                if(imageDeleteSuccess){
                                    deleteUser(activity,uid)
                                }else{
                                    Log.e("GoogleAuthUtil","프로필 사진 삭제 실패")
                                    deleteUser(activity,uid)
                                }
                            }
                        } else {
                            Log.e("GoogleAuthUtil", "Access revoke failed: ${it.exception?.message}", it.exception)
                            Toast.makeText(activity, "계정 접근 해제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("GoogleAuthUtil", "Account deletion failed: ${task.exception?.message}", task.exception)
                    Toast.makeText(activity, "계정 삭제에 실패했습니다: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.e("GoogleAuthUtil", "No current user found for deletion or UID is null.")
            Toast.makeText(activity, "현재 로그인된 사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    //재인증
    fun reAuthentification(activity: Activity, launcher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent) // launcher를 통해 재인증 프로세스 시작
    }

    fun handleReauthResult(data: Intent?, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.currentUser?.reauthenticate(credential)
                ?.addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        Log.d("GoogleAuthUtil", "Re-authentication successful")
                        onSuccess() // 재인증 성공 후 회원탈퇴 함수 호출
                    } else {
                        Log.e("GoogleAuthUtil", "Re-authentication failed: ${reauthTask.exception?.message}", reauthTask.exception)
                        onFailure() // 재인증 실패 시 처리
                    }
                }
        } catch (e: ApiException) {
            Log.e("GoogleAuthUtil", "Re-authentication failed: ${e.message}", e)
            onFailure()
        }
    }

    //회원가입도중 취소시 Authentication 삭제
    fun cancelSignUp(activity: Activity) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            currentUser.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SignUp", "Authentication 계정 삭제 성공")
                    Toast.makeText(activity, "회원가입을 취소했습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("SignUp", "Authentication 계정 삭제 실패: ${task.exception?.message}")
                    // 실패 처리 로직 (예: 사용자에게 실패 알림 표시)
                }
            }
        }
    }

    //회원탈퇴시 파이어스토어에서 사용자 데이터 삭제
    // Firestore에서 사용자 데이터 삭제 함수 수정

    fun deleteUser(activity: Activity,uid: String) {
        Log.d("GoogleAuthUtil", "deleteUser called") // 함수 호출 확인 로그
        val database = FirebaseFirestore.getInstance()
        Log.d("GoogleAuthUtil", "Attempting to delete Firestore data for UID: $uid")

        database.collection("user").document(uid)
            .delete()
            .addOnSuccessListener {
                Log.d("GoogleAuthUtil", "Firestore user data deleted successfully.")
                Toast.makeText(activity, "회원 정보가 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                navigateToLoginScreen(activity) // 로그인 화면으로 이동
            }
            .addOnFailureListener { exception ->
                Log.e("GoogleAuthUtil", "Failed to delete Firestore user data: ${exception.message}", exception)
                Toast.makeText(activity, "데이터 삭제에 실패했습니다: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun deleteProfileImage(uid: String, onComplete: (Boolean) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().getReference("profile_images/$uid.jpg")
        if (storageRef!= null){
            storageRef.delete()
                .addOnSuccessListener {
                    Log.d("GoogleAuthUtil", "프로필 이미지가 성공적으로 삭제되었습니다.")
                    onComplete(true)
                }
                .addOnFailureListener { exception ->
                    Log.e("GoogleAuthUtil", "프로필 이미지 삭제 실패: ${exception.message}", exception)
                    onComplete(false)
                }
        }

    }


    // 로그인 화면으로 이동하는 함수
    private fun navigateToLoginScreen(activity: Activity) {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
    }

    //로그 찍어보는 용
//    fun checkCurrentUser(activity: Activity){
//        val currentUser = firebaseAuth.currentUser?.uid ?: "없음"
//        Log.d("현재유저","${currentUser}")
//
//    }

}
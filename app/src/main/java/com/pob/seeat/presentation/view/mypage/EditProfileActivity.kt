package com.pob.seeat.presentation.view.mypage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pob.seeat.R
import com.pob.seeat.databinding.ActivityEditProfileBinding
import com.pob.seeat.domain.model.UserInfoModel
import com.pob.seeat.presentation.viewmodel.UserInfoViewModel
import com.pob.seeat.utils.GoogleAuthUtil.getUserUid
import com.pob.seeat.utils.ImageImplement.getCropOptions
import com.pob.seeat.utils.ImageImplement.launchImagePickerAndCrop
import com.pob.seeat.utils.ImageImplement.registerImageCropper
import com.pob.seeat.utils.ImageImplement.registerImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private val binding : ActivityEditProfileBinding by lazy { ActivityEditProfileBinding.inflate(layoutInflater) }
    private val userViewModel : UserInfoViewModel by viewModels()

    private var profileImageUri : Uri? = null

    private val storage = FirebaseStorage.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()

    private val pickImageLauncher = registerImagePicker(this@EditProfileActivity){uri ->
        if(uri != null){
            cropImageLauncher.launch(getCropOptions(uri))
        }
    }

    private val cropImageLauncher = registerImageCropper(this){uri ->
        profileImageUri = uri
        binding.ivEditProfileImage.setImageURI(profileImageUri)
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


    private fun  initView() = with(binding){

        val uid = getUserUid()
        if (uid != null) {
            userViewModel.getUserInfo(uid)
            observeUserInfo()
        } else {
            Log.e("userUid", "uid 조회 실패")
        }


        ivEditProfileImage.setOnClickListener {
            launchImagePickerAndCrop(pickImageLauncher, cropImageLauncher)
        }

        btnEditFinish.setOnClickListener {
            val uid = userViewModel.userInfo.value?.uid
            val nickname = etvEditNickname.text.toString().trim()
            val introduce =etvEditIntroduce.text.toString().trim()
            val profileUrl = profileImageUri

            if (nickname.isBlank()) {
                Toast.makeText(this@EditProfileActivity, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            if (introduce.isBlank()) {
                Toast.makeText(this@EditProfileActivity, "소개글을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }

            if(uid != null && profileUrl!= null){
                uploadProfileImage(profileImageUri!!, uid){profileUrl ->
                    if(profileUrl.isNotEmpty()){
                        userViewModel.editProfile(uid,nickname,introduce,profileUrl)
                        Toast.makeText(this@EditProfileActivity,"프로필 업데이트 완료",Toast.LENGTH_SHORT).show()

                        val intent = Intent()
                        intent.putExtra("updatedNickname", nickname)
                        intent.putExtra("updatedIntroduce", introduce)
                        intent.putExtra("updatedProfileUrl", profileUrl)
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }else{
                Toast.makeText(this@EditProfileActivity, "프로필 업데이트 실패", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun uploadProfileImage(imageUri: Uri, uid: String, callback: (String) -> Unit) {
        val file = storage.child("profile_images/$uid.jpg")
        file.putFile(imageUri)
            .addOnSuccessListener {
                file.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString()) // 성공 시 이미지 URL을 반환
                }
            }
            .addOnFailureListener { e ->
                Log.e("Upload Error", "이미지 업로드 실패 : ${e.message}")
                Toast.makeText(this@EditProfileActivity, "이미지 업로드에 실패했습니다", Toast.LENGTH_SHORT).show()
                callback("") // 실패 시 빈 문자열 반환
            }
    }


    private fun observeUserInfo() {
        lifecycleScope.launch {
            userViewModel.userInfo.collect { userInfo ->
                if (userInfo != null) {
                    Log.d("EditProfileActivity","${userInfo.uid}")
                    binding.etvEditNickname.setText(userInfo.nickname)
                    binding.etvEditIntroduce.setText(userInfo.introduce)

                    Glide.with(this@EditProfileActivity)
                        .load(userInfo.profileUrl)
                        .into(binding.ivEditProfileImage)
                }
            }
        }
    }


}
package com.pob.seeat.presentation.view.mypage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pob.seeat.R
import com.pob.seeat.databinding.ActivityEditProfileBinding
import com.pob.seeat.presentation.viewmodel.UserInfoViewModel
import com.pob.seeat.utils.GoogleAuthUtil.getUserUid
import com.pob.seeat.utils.ImageImplement.getCropOptions
import com.pob.seeat.utils.ImageImplement.launchImagePickerAndCrop
import com.pob.seeat.utils.ImageImplement.registerImageCropper
import com.pob.seeat.utils.ImageImplement.registerImagePicker
import com.pob.seeat.utils.Utils.compressBitmapToUri
import com.pob.seeat.utils.Utils.isValidNickname
import com.pob.seeat.utils.Utils.resizeImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private val binding: ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(
            layoutInflater
        )
    }
    private val userViewModel: UserInfoViewModel by viewModels()

    private var profileImageUri: Uri? = null

    private val pickImageLauncher = registerImagePicker(this@EditProfileActivity) { uri ->
        if (uri != null) {
            cropImageLauncher.launch(getCropOptions(uri))
        }
    }

    private val cropImageLauncher = registerImageCropper(this) { uri ->
        profileImageUri = uri
        profileImageUri?.let {
            uploadImageImmediately(it)
        }
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
        observeViewModel()
    }


    private fun initView() = with(binding) {

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

        etvEditNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().contains("\\s".toRegex())) {
                    tvNicknameRule.visibility = View.VISIBLE
                } else {
                    tvNicknameRule.visibility = View.GONE
                }
            }
        })

        btnEditFinish.setOnClickListener {
            when (userViewModel.profileUploadResult.value) {
                "LOADING" -> {
                    Toast.makeText(this@EditProfileActivity, "이미지 업로드까지 기다려주세요", Toast.LENGTH_SHORT)
                        .show()
                }

                null -> {
                    val uid = userViewModel.userInfo.value?.uid
                    val nickname = etvEditNickname.text.toString().trim()
                    val introduce = etvEditIntroduce.text.toString().trim()

                    if (nickname.isValidNickname()) {

                        if (uid != null) {

                            userViewModel.editProfile(uid, nickname, introduce)
                            Toast.makeText(
                                this@EditProfileActivity,
                                "프로필 업데이트 완료",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent()
                            intent.putExtra("updatedNickname", nickname)
                            intent.putExtra("updatedIntroduce", introduce)
                            intent.putExtra("updatedProfileUrl", profileImageUri.toString())
                            setResult(Activity.RESULT_OK, intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@EditProfileActivity,
                                "프로필 업데이트 실패",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            this@EditProfileActivity,
                            "유효하지 않은 닉네임입니다. 다시 확인해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                    if (introduce.isBlank()) {
                        Toast.makeText(this@EditProfileActivity, "소개글을 입력해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    }

                }

                else -> {
                    val uid = userViewModel.userInfo.value?.uid
                    val nickname = etvEditNickname.text.toString().trim()
                    val introduce = etvEditIntroduce.text.toString().trim()

                    if (nickname.isBlank()) {
                        Toast.makeText(this@EditProfileActivity, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    if (introduce.isBlank()) {
                        Toast.makeText(this@EditProfileActivity, "소개글을 입력해주세요.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    if (uid != null) {

                        userViewModel.editProfile(uid, nickname, introduce)
                        Toast.makeText(this@EditProfileActivity, "프로필 업데이트 완료", Toast.LENGTH_SHORT)
                            .show()

                        val intent = Intent()
                        intent.putExtra("updatedNickname", nickname)
                        intent.putExtra("updatedIntroduce", introduce)
                        intent.putExtra("updatedProfileUrl", profileImageUri.toString())

                        setResult(Activity.RESULT_OK, intent)
                        finish()

                    } else {
                        Toast.makeText(this@EditProfileActivity, "프로필 업데이트 실패", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            }


        }

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            userViewModel.profileUploadResult.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { imageUrl ->
                    binding.pbBackground.visibility = View.GONE // 업로드가 완료되면 프로그래스 바 숨김
                    binding.pbEditPhoto.visibility = View.GONE

                    when {
                        imageUrl == "LOADING" -> {
                            // 업로드 중에는 아무 메시지도 표시하지 않음
                            binding.pbBackground.visibility = View.VISIBLE
                            binding.pbEditPhoto.visibility = View.VISIBLE
                        }

                        imageUrl.isNullOrBlank() -> {
                            if (imageUrl == null) {
                                // 초기 상태이므로 아무것도 하지 않음
                                return@collect
                            } else {
                                // 업로드 실패 처리
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "프로필 사진 업로드에 실패했습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        else -> {
                            // 업로드 성공 처리
                            Log.d("ImageUpload", "이미지 업로드 성공: $imageUrl")
                            Toast.makeText(
                                this@EditProfileActivity,
                                "프로필 사진 업로드를 완료했습니다.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
        }
    }

    private fun uploadImageImmediately(uri: Uri) {
        // 프로그래스 바 표시
        binding.pbBackground.visibility = View.VISIBLE
        binding.pbEditPhoto.visibility = View.VISIBLE

        val uid = userViewModel.userInfo.value?.uid

        // 이미지 리사이즈 및 압축
        val resizedBitmap = resizeImage(this@EditProfileActivity, uri)
        val compressedUri = compressBitmapToUri(this@EditProfileActivity, resizedBitmap)

        // ViewModel을 통해 이미지 업로드
        userViewModel.uploadProfileImage(compressedUri, uid!!)

    }

//    private fun uploadProfileImage(imageUri: Uri, uid: String, callback: (String) -> Unit) {
//        val file = storage.child("profile_images/$uid.jpg")
//        file.putFile(imageUri)
//            .addOnSuccessListener {
//                file.downloadUrl.addOnSuccessListener { uri ->
//                    callback(uri.toString()) // 성공 시 이미지 URL을 반환
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("Upload Error", "이미지 업로드 실패 : ${e.message}")
//                Toast.makeText(this@EditProfileActivity, "이미지 업로드에 실패했습니다", Toast.LENGTH_SHORT).show()
//                callback("") // 실패 시 빈 문자열 반환
//            }
//    }


    private fun observeUserInfo() {
        lifecycleScope.launch {
            userViewModel.userInfo.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { userInfo ->
                    if (userInfo != null) {
                        Log.d("EditProfileActivity", "${userInfo.uid}")
                        binding.etvEditNickname.setText(userInfo.nickname)
                        binding.etvEditIntroduce.setText(userInfo.introduce)

                        Glide.with(this@EditProfileActivity)
                            .load(userInfo.profileUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(binding.ivEditProfileImage)
                        Log.d("사진", "${userInfo.profileUrl}")
                    }
                }
        }
    }


}
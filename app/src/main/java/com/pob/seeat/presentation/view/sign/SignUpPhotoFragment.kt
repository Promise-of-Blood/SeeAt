package com.pob.seeat.presentation.view.sign

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pob.seeat.databinding.FragmentSignUpPhotoBinding
import com.pob.seeat.presentation.viewmodel.UserInfoViewModel
import com.pob.seeat.utils.ImageImplement.getCropOptions
import com.pob.seeat.utils.ImageImplement.launchImagePickerAndCrop
import com.pob.seeat.utils.ImageImplement.registerImageCropper
import com.pob.seeat.utils.ImageImplement.registerImagePicker
import com.pob.seeat.utils.Utils.compressBitmapToUri
import com.pob.seeat.utils.Utils.resizeImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class SignUpPhotoFragment : Fragment() {

    private var _binding: FragmentSignUpPhotoBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserInfoViewModel by activityViewModels()

    private var pickImageUri: Uri? = null

    private val pickImageLauncher = registerImagePicker(this) { uri ->
        if (uri != null) {
            cropImageLauncher.launch(getCropOptions(uri))
        }
    }

    private val cropImageLauncher = registerImageCropper(this) { uri ->
        pickImageUri = uri
        pickImageUri?.let {
            uploadImageImmediately(it)
        }
        binding.icCamera.setImageURI(pickImageUri)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() = with(binding) {
        btnSignupNext.setOnClickListener {

            userViewModel.saveTempUserInfo(profileUrl =userViewModel.profileUploadResult.value)

            if(userViewModel.profileUploadResult.value?.isNotBlank() == true){
                (activity as? SignUpActivity)?.let { activity ->
                    activity.signUpBinding.vpSignUp.currentItem += 1
                }
            }else {
                Toast.makeText(requireContext(), "이미지 업로드 완료까지 기다려주세요", Toast.LENGTH_SHORT).show()

            }
        }


        flImageInput.setOnClickListener {
            launchImagePickerAndCrop(pickImageLauncher, cropImageLauncher)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            userViewModel.profileUploadResult.collect { imageUrl ->
                binding.clPb.visibility = View.GONE // 업로드가 완료되면 프로그래스 바 숨김

                when {
                    imageUrl == "LOADING" -> {
                        // 업로드 중에는 아무 메시지도 표시하지 않음
                        binding.clPb.visibility = View.VISIBLE
                    }
                    imageUrl.isNullOrBlank() -> {
                        if (imageUrl == null) {
                            // 초기 상태이므로 아무것도 하지 않음
                            return@collect
                        } else {
                            // 업로드 실패 처리
                            Toast.makeText(requireContext(), "프로필 사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> {
                        // 업로드 성공 처리
                        Log.d("ImageUpload", "이미지 업로드 성공: $imageUrl")
                        Toast.makeText(requireContext(), "프로필 사진 업로드를 완료했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    private fun uploadImageImmediately(uri: Uri) {
        // 프로그래스 바 표시
        binding.clPb.visibility = View.VISIBLE

        val uid = userViewModel.tempUserInfo.value?.uid ?: return

        // 이미지 리사이즈 및 압축
        val resizedBitmap = resizeImage(requireContext(),uri)
        val compressedUri = compressBitmapToUri(requireContext(),resizedBitmap)

        // ViewModel을 통해 이미지 업로드
        userViewModel.uploadProfileImage(compressedUri, uid)

    }



}
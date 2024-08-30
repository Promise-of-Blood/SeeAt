package com.pob.seeat.presentation.view.sign

import android.app.Activity
import android.content.Intent
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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.canhub.cropper.CropImageActivity
import com.pob.seeat.R
import com.pob.seeat.databinding.FragmentSignUpNameBinding
import com.pob.seeat.databinding.FragmentSignUpPhotoBinding
import com.pob.seeat.domain.model.TempUserInfo
import com.pob.seeat.domain.model.UserInfoModel
import com.pob.seeat.presentation.viewmodel.UserInfoViewModel
import com.pob.seeat.utils.ImageImplement.getCropOptions
import com.pob.seeat.utils.ImageImplement.launchImagePickerAndCrop
import com.pob.seeat.utils.ImageImplement.registerImageCropper
import com.pob.seeat.utils.ImageImplement.registerImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() = with(binding) {
        btnSignupNext.setOnClickListener {
            //클릭했을때 다음 페이지로 넘어가기 + 사진 url 저장하기
            val profileUrl = pickImageUri.toString()

            if (profileUrl.isNotBlank()) {
                userViewModel.saveTempUserInfo(profileUrl = profileUrl)

                Log.d("TempUserInfo","tempUserInfo : ${userViewModel.tempUserInfo.value}")

                (activity as? SignUpActivity)?.let { activity ->
                    activity.signUpBinding.vpSignUp.currentItem += 1
                }
            } else {
                Toast.makeText(requireContext(), "프로필 사진을 설정해 주세요", Toast.LENGTH_SHORT).show()
            }

            //사진 설정안되어있으면 다이얼로그 띄우기
        }

        flImageInput.setOnClickListener {
            launchImagePickerAndCrop(pickImageLauncher,cropImageLauncher)
        }
    }

    private fun setUriAsBackground(uri: Uri) {
        // Uri로부터 InputStream을 가져와 Bitmap으로 변환
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        // Bitmap을 Drawable로 변환
        val drawable = BitmapDrawable(resources, bitmap)

        // Drawable을 View의 배경으로 설정
        binding.flImageInput.background = drawable

        // InputStream 닫기
        inputStream?.close()
    }


}
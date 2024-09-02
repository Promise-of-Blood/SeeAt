package com.pob.seeat.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
object ImageImplement {
    fun registerImagePicker(fragment: Fragment, onImagePicked: (Uri?) -> Unit): ActivityResultLauncher<PickVisualMediaRequest>{
        return fragment.registerForActivityResult(ActivityResultContracts.PickVisualMedia()){uri ->
            onImagePicked(uri)
        }
    }

    fun registerImageCropper(
        fragment: Fragment,
        onCropCompleted: (Uri?) -> Unit
    ): ActivityResultLauncher<CropImageContractOptions> {
        return fragment.registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                onCropCompleted(result.uriContent)
            } else {
                // 에러 처리 로직 추가
                val exception = result.error
            }
        }
    }

    fun launchImagePickerAndCrop(
        pickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>,
        cropImageLauncher: ActivityResultLauncher<CropImageContractOptions>
    ) {
        pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun getCropOptions(uri: Uri): CropImageContractOptions {
        return CropImageContractOptions(
            uri = uri,
            cropImageOptions = CropImageOptions(
                outputCompressFormat = Bitmap.CompressFormat.PNG,
                minCropResultHeight = 50,
                minCropResultWidth = 50,
                aspectRatioY = 5,
                aspectRatioX = 5,
                fixAspectRatio = true,
                borderLineColor = Color.GREEN
            )
        )
    }

}


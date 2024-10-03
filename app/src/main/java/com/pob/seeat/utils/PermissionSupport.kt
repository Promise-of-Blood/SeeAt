package com.pob.seeat.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pob.seeat.utils.dialog.Dialog

object PermissionSupport {
    private const val MULTIPLE_PERMISSIONS = 1023 // 위치 권한 요청 코드
    const val RETRY_PERMISSIONS = 1024 // 재요청 코드

    // 요청할 권한 배열 저장
    private val permissionNeeded = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )
    private val permissions = permissionNeeded.toMutableList().apply {
        // API 33 이상에서만 POST_NOTIFICATIONS 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // 배경 Context 및 Activity 참조 필요 시 설정
    fun checkPermission(activity: Activity): Boolean {
        val permissionList = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                activity, permission
            ) != PackageManager.PERMISSION_GRANTED
        }

        return if (permissionList.isNotEmpty()) {
            // 권한이 허용되지 않은 것이 있으면 요청
            ActivityCompat.requestPermissions(
                activity, permissionList.toTypedArray(), MULTIPLE_PERMISSIONS
            )
            false
        } else {
            // 모든 권한이 허용되었을 때
            true
        }
    }

    // 요청한 권한에 대한 결과값 판단 및 처리
    fun permissionResult(requestCode: Int, grantResults: IntArray): Boolean {
        return when (requestCode) {
            MULTIPLE_PERMISSIONS, RETRY_PERMISSIONS -> {
                // -1 (거부된 권한)이 있는지 체크하여 거부된 것이 있다면 false 반환
                return permissionNeeded.filterIndexed { index, _ ->
                    grantResults[index] == PackageManager.PERMISSION_DENIED
                }.isEmpty()
            }

            else -> false
        }
    }

    // 권한 요청 이유 설명하는 다이얼로그
    private fun showPermissionRationaleDialog(activity: Activity) {
        Dialog.showDialog(
            activity,
            "권한이 필요합니다",
            "앱 기능을 사용하려면 위치 권한이 필요합니다. 계속하려면 권한을 허용해 주세요.",
        ) {
            // 권한 재요청
            ActivityCompat.requestPermissions(
                activity, permissionNeeded.toTypedArray(), RETRY_PERMISSIONS
            )
        }
    }

    // 위치 권한 체크 후 권한 재요청
    fun checkLocationPermissions(activity: Activity) {
        if (permissionNeeded.any {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, it
                )
            }) {
            // 위치 권한이 하나라도 거부된 경우, 다시 요청
            showPermissionRationaleDialog(activity)
        }
    }
}

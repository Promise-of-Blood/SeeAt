package com.pob.seeat.presentation.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pob.seeat.R
import com.pob.seeat.databinding.ActivityPermissionGuideBinding
import com.pob.seeat.presentation.view.sign.LoginActivity
import com.pob.seeat.utils.PermissionSupport

class PermissionGuideActivity : AppCompatActivity() {
    private var isRequested: Boolean = false

    private val binding: ActivityPermissionGuideBinding by lazy {
        ActivityPermissionGuideBinding.inflate(
            layoutInflater
        )
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

    private fun initView() = with(binding) {
        binding.btnPermissionGuide.setOnClickListener {
            PermissionSupport.checkPermission(this@PermissionGuideActivity)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionSupport.permissionResult(requestCode, grantResults)) {
            // 권한을 허용한 경우
            if (isRequested) Toast.makeText(
                this@PermissionGuideActivity, "위치 권한 사용을 거부했습니다.", Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this@PermissionGuideActivity, LoginActivity::class.java)
            startActivity(intent)
        } else {
            // 권한을 거부한 경우
            Toast.makeText(this, "앱 실행을 위해서는 권한을 설정해야 합니다.", Toast.LENGTH_SHORT).show()
            PermissionSupport.checkLocationPermissions(this@PermissionGuideActivity)
            isRequested = true
        }
    }
}


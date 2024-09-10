package com.pob.seeat.presentation.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pob.seeat.R
import com.pob.seeat.databinding.ActivityPermissionGuideBinding
import com.pob.seeat.presentation.view.sign.LoginActivity

class PermissionGuideActivity : AppCompatActivity() {

    private val binding : ActivityPermissionGuideBinding by lazy { ActivityPermissionGuideBinding.inflate(layoutInflater) }

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






    private fun initView() = with(binding){

        binding.btnPermissionGuide.setOnClickListener {
            val intent = Intent(this@PermissionGuideActivity,LoginActivity::class.java)
            startActivity(intent)
        }
    }
}


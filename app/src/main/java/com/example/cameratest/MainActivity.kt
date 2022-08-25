package com.example.cameratest

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.cameratest.databinding.ActivityMainBinding
import com.example.cameratest.presentation.base.BaseActivity
import com.example.cameratest.presentation.customcamera.CustomCameraActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission()
    }

    // 권한 묻기
    private fun checkPermission() {
        TedPermission.create()
            .setPermissions(Manifest.permission.CAMERA)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    Toast.makeText(applicationContext, "권한 허용", Toast.LENGTH_SHORT).show()
                    setBtnClickListener()   // 버튼 클릭 리스너 달기
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(applicationContext, "권한 거절", Toast.LENGTH_SHORT).show()
                }

            })
            .setDeniedMessage("""
                    If you reject permission,you can not use this service.
                    Please turn on permissions at [Setting] > [Permission]
                """.trimIndent())
            .check()
    }

    // '사진 찍기' 버튼 클릭 시 사진 찍는 액티비티로 이동
    private fun setBtnClickListener() {
        binding.btnTakePhoto.setOnClickListener {
            startActivity(Intent(this@MainActivity, CustomCameraActivity::class.java))
        }
    }
}
package com.example.cameratest

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.cameratest.databinding.ActivityMainBinding
import com.example.cameratest.presentation.base.BaseActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission()
    }

    private fun checkPermission() {
        TedPermission.create()
            .setPermissions(Manifest.permission.CAMERA)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    Toast.makeText(applicationContext, "권한 허용", Toast.LENGTH_SHORT).show()
                    setBtnClickListener()
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

    private fun setBtnClickListener() {
        binding.btnTakePhoto.setOnClickListener {

        }
    }
}
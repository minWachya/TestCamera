package com.example.cameratest.presentation.customcamera

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.example.cameratest.R
import com.example.cameratest.databinding.ActivityCustomCameraBinding
import com.example.cameratest.presentation.base.BaseActivity


class CustomCameraActivity: BaseActivity<ActivityCustomCameraBinding>(R.layout.activity_custon_camera) {
    // 2. 그다음 시작하는게 onCreate. Activity 생성시 잴 먼저 실행된다.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        setContentView(R.layout.cameratest)
//        imageview = findViewById<View>(R.id.imageView1) as ImageView
//        surfaceView = findViewById<View>(R.id.surfaceView1) as SurfaceView
//        surfaceHolder = surfaceView.getHolder()
//        surfaceHolder.addCallback(surfaceListener)
        binding.btnTakePhoto.setOnClickListener(
//            if (camera != null && inProgress === false) {
//                camera.takePicture(null, null, takePicture)
//                inProgress = true
//            }
        )
    }


//    private val takePicture: PictureCallback = object : PictureCallback() {
//        fun onPictureTaken(data: ByteArray?, camera: Camera) {
//            // TODO Auto-generated method stub
//            Log.i(TAG, "샷다 누름 확인")
//            if (data != null) Log.i(TAG, "JPEG 사진 찍었음!")
//            val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
//            imageview.setImageBitmap(bitmap)
//            camera.startPreview()
//            inProgress = false
//        }
//    }
//
//    private val surfaceListener: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
//        override fun surfaceDestroyed(holder: SurfaceHolder) {
//            // TODO Auto-generated method stub
//            camera.release()
//            camera = null
//            Log.i(TAG, "카메라 기능 해제")
//        }
//
//        override fun surfaceCreated(holder: SurfaceHolder) {
//            // TODO Auto-generated method stub
//            camera = Camera.open()
//            Log.i(TAG, "카메라 미리보기 활성")
//            try {
//                camera.setPreviewDisplay(holder)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//
//        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
//            // TODO Auto-generated method stub
//            val parameters: Camera.Parameters = camera.getParameters()
//            parameters.setPreviewSize(width, height)
//            camera.startPreview()
//            Log.i(TAG, "카메라 미리보기 활성")
//        }
//    }
}
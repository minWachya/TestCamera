package com.example.cameratest.presentation.customcamera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.*
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.cameratest.R
import com.example.cameratest.databinding.ActivityCustomCameraBinding
import com.example.cameratest.presentation.base.BaseActivity
import java.io.*

class CustomCameraActivity: BaseActivity<ActivityCustomCameraBinding>(R.layout.activity_custom_camera) {
    private var mHandler: Handler? = null
    private lateinit var mPreviewBuilder: CaptureRequest.Builder
    private lateinit var mImageReader: ImageReader
    private lateinit var mCameraDevice: CameraDevice
    private var fileCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 화면 켜짐 유지
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        initView()
        setBtnTakePhotoClickListener()
    }

    // View 초기화
    private fun initView() {
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                initCameraAndPreview()
            }
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mCameraDevice.close()
            }
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int, ) { }
        })
    }

    // <Take Photo> 버튼 클릭 리스너
    private fun setBtnTakePhotoClickListener() {
        binding.btnTakePhoto.setOnClickListener {
            takePicture() // 사진 찍기
        }
    }

    // 카메라와 미리보기 초기화
    private fun initCameraAndPreview() {
        val handlerThread = HandlerThread("CAMERA2")
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)
        openCamera()
    }

    // 카메라 열기
    private fun openCamera() {
        try {
            val mCameraManager = this.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val characteristics = mCameraManager.getCameraCharacteristics("0")
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val largestPreviewSize = map!!.getOutputSizes(ImageFormat.JPEG)[0]
            mImageReader = ImageReader.newInstance(
                largestPreviewSize.width,
                largestPreviewSize.height,
                ImageFormat.JPEG,
                7
            )
            if (ActivityCompat.checkSelfPermission(this@CustomCameraActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) return
            mCameraManager.openCamera("0", deviceStateCallback, mHandler)
        } catch (e: CameraAccessException) {
            Log.d("mmm", "카메라를 열지 못했습니다.")
        }
    }

    // 카메라 열 때 콜백: 미리보기 보여주기
    private val deviceStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) takePreview()
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }
        override fun onError(camera: CameraDevice, error: Int) {
            Log.d("mmm", "카메라를 열지 못했습니다.")
        }
    }

    // 미리보기 보여주기
    @RequiresApi(Build.VERSION_CODES.P)
    @Throws(CameraAccessException::class)
    private fun takePreview() {
        mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        mPreviewBuilder.addTarget(binding.surfaceView.holder.surface)
        val outputSurface = arrayListOf<Surface>(binding.surfaceView.holder.surface, mImageReader.surface)
        mCameraDevice.createCaptureSession(outputSurface, mSessionPreviewStateCallback, mHandler)
    }

    // 사진찍을 때 호출하는 메서드
    private fun takePicture() {
        try {
            // 이미지 저장하고, 화면에 띄우기
            saveImageFile()
            // mSessionPreviewStateCallback2를 통해 이미지 캡쳐 후 카메라 미리보기 세션 재시작
            val outputSurface = arrayListOf<Surface>(mImageReader.surface)
            mCameraDevice.createCaptureSession(outputSurface, mSessionPreviewStateCallback2, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    // 이미지 저장하고 화면에 띄우는 메소드
    private fun saveImageFile() {
        // 캡쳐한 이미지를 저장할 파일 생성
        val file = File(Environment.getExternalStorageDirectory().toString() + "/pic${fileCount}.jpg")
        val readerListener = ImageReader.OnImageAvailableListener {
            var image : Image? = null
            try {
                image = mImageReader.acquireLatestImage()
                val buffer = image!!.planes[0].buffer
                val bytes = ByteArray(buffer.capacity())
                buffer.get(bytes)
                var output: OutputStream? = null
                try {
                    output = FileOutputStream(file)
                    output.write(bytes)
                } finally {
                    output?.close()
                    // 촬영한 이미지 화면에 띄우기
                    val bitmap: Bitmap = BitmapFactory.decodeFile(file.path)
                    val rotateMatrix = Matrix() // 비트맵 사진이 90도 돌아가있는 문제를 해결하기 위해 90도 rotate
                    rotateMatrix.postRotate(90F)
                    val rotatedBitmap: Bitmap = Bitmap.createBitmap(bitmap, 0,0, bitmap.width, bitmap.height, rotateMatrix, false)
                    binding.ivPhoto.setImageBitmap(rotatedBitmap)

                    fileCount++
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                image?.close()
            }
        }
        // 이미지 저장
        mImageReader.setOnImageAvailableListener(readerListener, null)
    }

    // 미리보기 콜백
    private val mSessionPreviewStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            try {
                // 오토포커싱 계속 동작
                mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                session.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
        override fun onConfigureFailed(session: CameraCaptureSession) {
            Toast.makeText(this@CustomCameraActivity, "카메라 구성 실패", Toast.LENGTH_SHORT).show()
        }
    }
    // 이미지 캡쳐 후 카메라 미리보기 재시작
    private val mSessionPreviewStateCallback2 = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            try {
                // 캡쳐하기
                val captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                captureBuilder.addTarget(mImageReader.surface)
                captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
                session.capture(captureBuilder.build(), captureListener, null)
                // 미리보기 재시작
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) takePreview()
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
        override fun onConfigureFailed(session: CameraCaptureSession) {
            Toast.makeText(this@CustomCameraActivity, "카메라 구성 실패", Toast.LENGTH_SHORT).show()
        }
    }

    // 캡쳐 리스너
    private val captureListener = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
            super.onCaptureCompleted(session, request, result)
            Toast.makeText(applicationContext, "사진이 촬영되었습니다", Toast.LENGTH_SHORT).show()
        }
    }

}
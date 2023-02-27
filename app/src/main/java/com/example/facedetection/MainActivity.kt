package com.example.facedetection

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.FaceDetector
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.ImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageAnalysis: ImageAnalysis

    companion object {
        const val TAG = "CameraXBasic"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 10)
            startCamera()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    fun startCamera() {
        val finder = findViewById<PreviewView>(R.id.viewFinder)
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()

            var preview: Preview = Preview.Builder()
                .build()

            var cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val context = this
            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), ImageAnalyzer(context = context, attrs = null))

            try {
                cameraProvider.unbindAll()
                var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageAnalysis, preview,)
                Log.d(TAG, "camera set.")
                preview.setSurfaceProvider(finder.createSurfaceProvider(camera.cameraInfo))
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }
}

package com.example.facedetection

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MainActivity : AppCompatActivity() {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageAnalysis: ImageAnalysis

    companion object {
        const val TAG = "CameraXBasic"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 10
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    fun startCamera() {
        val scope = CoroutineScope(Dispatchers.IO)
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
            imageAnalysis.setAnalyzer(
                Executors.newSingleThreadExecutor(), ImageAnalyzer()
//                { imageProxy ->
//                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
//                    // insert your code here.
//                    Log.d(TAG, "setAnalyzer。")
//
//                    // after done, release the ImageProxy object
//                    imageProxy.close()
//                }
            )

            try {
                cameraProvider.unbindAll()
                Log.d(TAG, "1。")


                var camera =
                    cameraProvider.bindToLifecycle(
                        this as LifecycleOwner,
                        cameraSelector,
                        imageAnalysis,
                        preview,
                    )
                Log.d(TAG, "2。")

                preview.setSurfaceProvider(finder.createSurfaceProvider(camera.cameraInfo))
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                Log.d(TAG, "。")
            }
        }, ContextCompat.getMainExecutor(this))
    }


//    private fun setAnalyzer(executor: Executor) {
////        return suspendCoroutine { continuation ->
//
//        imageAnalysis.setAnalyzer(executor, ImageAnalysis.Analyzer { imageProxy ->
////            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
////               continuation.resume(imageProxy)
//            imageProxy.close()
//        })
//
//        imageAnalysis.clearAnalyzer()
//       }
}
//}

package com.example.facedetection

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ImageAnalyzer(context: Context?, attrs: AttributeSet?) : ImageAnalysis.Analyzer,
    View(context, attrs) {

    init {
        Log.d(MainActivity.TAG, "analyzer-log init...")
    }

    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    private var bounds = Rect(0, 0, 0, 0)
    private var bounds2 = Rect(0, 0, 0, 0)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        Log.d(MainActivity.TAG, "analyze start.")
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            Log.d(MainActivity.TAG, "image true.")
            val image =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val realTimeOpts = FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()
            val detector = FaceDetection.getClient(realTimeOpts)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    bounds = Rect(0, 0, 0, 0)
                    Log.d(TAG, "pcocess success")
                    for (face in faces) {
                        bounds = face.boundingBox
                        Log.d(TAG, "Face detected:$bounds")
                    }
                    invalidate()
                    imageProxy.close()
                    Log.d(MainActivity.TAG, "imagePrroxy closed.")
                }
                .addOnFailureListener { e ->
                    Log.d(MainActivity.TAG, "process failed.")
                }
        } else {
            Log.d(MainActivity.TAG, "image false.")
        }
        Log.d(TAG, "Analyzer finish")

    }

    override fun onDraw(canvas: Canvas?) {
        Log.d(TAG, "bounds:$bounds")
        canvas?.drawRect(
            bounds.left.toFloat(),
            bounds.top.toFloat(),
            bounds.right.toFloat(),
            bounds.bottom.toFloat(),
            paint
        )
        Log.d(TAG, "onDraw called")
    }
}
package com.example.facedetection

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import java.util.*
import kotlin.collections.ArrayList

class ImageAnalyzer(context: Context?, attrs: AttributeSet?) : ImageAnalysis.Analyzer,
    View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private var bounds = Rect(500, 1000, 500, 600)


//    private var a: Int = 0
//    private var b: Int = 0
//    private var c: Int = 0
//    private var d: Int = 0

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        Log.d(MainActivity.TAG, "analyze start.")
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            Log.d(MainActivity.TAG, "image true.")
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val realTimeOpts = FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()
            val detector = FaceDetection.getClient(realTimeOpts)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    for (face in faces) {
                        bounds.set(
                            face.boundingBox.left,
                            face.boundingBox.top,
                            face.boundingBox.right,
                            face.boundingBox.bottom
                        )
                        Log.d(TAG, "Face detected:$bounds")
                    }
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
        super.onDraw(canvas)
        Log.d(TAG, "bounds:$bounds")
        canvas?.drawRect(bounds, paint)
        invalidate()
    }
}


//    init {
//        generateRandomNumbers()
//    }
//
//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//        Log.d(TAG, "face paint")
//        updateView()
//        canvas?.drawRect(a.toFloat(), b.toFloat(), c.toFloat(), d.toFloat(), paint)
//    }
//
//    fun updateView() {
//        generateRandomNumbers()
//        invalidate()
//    }
//
//    private fun generateRandomNumbers() {
//        a = (100..600).random()
//        b = (100..600).random()
//        c = (100..600).random()
//        d = (100..600).random()
//    }




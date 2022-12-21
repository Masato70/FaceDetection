package com.example.facedetection

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Paint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ImageAnalyzer : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        Log.d(MainActivity.TAG, "analyze start.")
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            Log.d(MainActivity.TAG, "image true.")
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val detector = FaceDetection.getClient()

            FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()

            detector.process(image)
                .addOnSuccessListener { faces ->
                    Log.d(MainActivity.TAG, "process success.")

                    for (face in faces) {
                        face.getContour(FaceContour.LEFT_EYE)?.points
                        face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points
                        Log.d(MainActivity.TAG, "Successful face detection")
                    }
                    imageProxy.close()
                    Log.d(MainActivity.TAG, "imagePrroxy closed.")
                }
                .addOnFailureListener { e ->
                    Log.d(MainActivity.TAG, "process failed.")
                }
        }
    }
}
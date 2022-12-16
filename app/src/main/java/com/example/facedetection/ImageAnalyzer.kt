package com.example.facedetection

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ImageAnalyzer : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {

        val scope = CoroutineScope(Dispatchers.IO)

        Log.d(MainActivity.TAG, "3。")
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            Log.d(MainActivity.TAG, "4。")
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val detector = FaceDetection.getClient()


            FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()

            detector.process(image)
                .addOnSuccessListener { faces ->
                    Log.d(MainActivity.TAG, "5。")
                    for (face in faces) {
                        face.getContour(FaceContour.LEFT_EYE)?.points
                        face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points
                        Log.d(MainActivity.TAG,"6。")
                    }
                    Log.d(MainActivity.TAG,"7。")
                }
                .addOnFailureListener { e ->
                    Log.d(MainActivity.TAG,"失敗しました。")
                }
        }
//        imageProxy.close()
    }
}

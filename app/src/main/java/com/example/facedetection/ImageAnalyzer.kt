package com.example.facedetection

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.*
import android.util.Log
import android.widget.ImageView
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class ImageAnalyzer : ImageAnalysis.Analyzer {


    interface OnBitmapReadyListener {
        fun onBitmapReady(bitmap: Bitmap)
    }

    private var listener: OnBitmapReadyListener? = null

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        Log.d(MainActivity.TAG, "analyze start.")
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            Log.d(MainActivity.TAG, "image true.")
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val options = FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()
            val detector = FaceDetection.getClient(options)

            detector.process(image)
                .addOnSuccessListener { faces ->
                    Log.d(TAG, "process success.")
                    for (face in faces) {
//                        val faceContours = face.getContour(FaceContour.FACE)?.points
                        val paint = Paint().apply {
                            color = Color.GREEN
                            style = Paint.Style.STROKE
                            strokeWidth = 5f
                        }
                        val buffer = mediaImage.planes[0].buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)
                        val bitmap = Bitmap.createBitmap(
                            mediaImage.width,
                            mediaImage.height,
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)

//                        if (faceContours != null) {
                        Log.d(TAG, "Number of faces detected:")
//                            for (contour in faceContours) {
//                            faceContours.forEachIndexed { index, pointF ->
                        val bounds = Rect(
                            face.boundingBox.left,
                            face.boundingBox.top,
                            face.boundingBox.right,
                            face.boundingBox.bottom
                        )
                        Log.d(TAG, "Face detected at: left: " + bounds.left + ", top: " + bounds.top + ", right: " + bounds.right + ", bottom: " + bounds.bottom)
                        canvas.drawRect(bounds, paint)
//                            }
                        listener?.onBitmapReady(bitmap)
//                        }
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


    fun setOnBitmapReadyListener(listener: OnBitmapReadyListener) {
        this.listener = listener
    }

}


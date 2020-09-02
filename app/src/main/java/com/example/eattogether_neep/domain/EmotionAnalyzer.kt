package com.example.eattogether_neep.domain

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector

private typealias Listener = (image: FirebaseVisionImage, faces: List<FirebaseVisionFace>) -> Unit

class EmotionAnalyzer(
    private val detector: FirebaseVisionFaceDetector
) : ImageAnalysis.Analyzer {

    private var listener: Listener? = null

    fun onFacesDetected(listener: Listener) {
        this.listener = listener
    }

    private fun degreesToFirebaseRotation(degrees: Int): Int = when (degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(image: ImageProxy) {
        image.image
            ?.let { mediaImage ->
                FirebaseVisionImage.fromMediaImage(
                    mediaImage,
                    degreesToFirebaseRotation(image.imageInfo.rotationDegrees)
                )
            }
            ?.let { firebaseImage ->
                detector.detectInImage(firebaseImage)
                    .addOnSuccessListener { faces -> listener?.invoke(firebaseImage, faces) }
                    .addOnCompleteListener { image.close() }
            }
    }
}

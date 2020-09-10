package com.example.eattogether_neep.emotion.facedetection

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.vision.face.FaceDetector.ACCURATE_MODE
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.*

internal class FaceDetector(cameraWidth: Int, cameraHeight: Int, enableContours: Boolean = true,
                            private val successListener: OnSuccessListener<in List<FirebaseVisionFace>>,
                            private val failureListener: OnFailureListener) {

    private val detector: FirebaseVisionFaceDetector
    private val metadata: FirebaseVisionImageMetadata

    init {
        val detectorOptions = Builder()
            .setPerformanceMode(ACCURATE_MODE)
            .setLandmarkMode(if (enableContours) NO_LANDMARKS else ALL_LANDMARKS)
            .setClassificationMode(ALL_CLASSIFICATIONS)
            .setContourMode(if (enableContours) ALL_CONTOURS else NO_CONTOURS)
            .setMinFaceSize(0.1F)
            .enableTracking()
            .build()


        detector = FirebaseVision.getInstance().getVisionFaceDetector(detectorOptions)
        metadata = FirebaseVisionImageMetadata.Builder()
            .setWidth(cameraWidth)
            .setHeight(cameraHeight)
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setRotation(FirebaseVisionImageMetadata.ROTATION_270)
            .build()
    }


    fun detectFromByteArray(byteArray: ByteArray) {
        detect(FirebaseVisionImage.fromByteArray(byteArray, metadata))
    }

    private fun detect(firebaseVisionImage: FirebaseVisionImage) {
        detector.detectInImage(firebaseVisionImage)
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener)
    }
}
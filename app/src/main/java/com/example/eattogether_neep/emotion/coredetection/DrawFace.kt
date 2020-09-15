package com.example.eattogether_neep.emotion.coredetection

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark

internal class DrawFace(private val cameraWidth: Int, private val cameraHeight: Int, private val enableContours: Boolean = true) {

    fun showVisionDetection(faces: List<FirebaseVisionFace>): Bitmap {
        val contourFaces = ArrayList<ContourFace>()
        val landmarkFaces = ArrayList<LandmarkFace>()

        faces.forEach {
            if (enableContours) {
                val contourFace = ContourFace(
                    boundingBox = it.boundingBox,
                    faceOvalContour = it.getContour(FirebaseVisionFaceContour.FACE),
                    leftEyeContour = it.getContour(FirebaseVisionFaceContour.LEFT_EYE),
                    rightEyeContour = it.getContour(FirebaseVisionFaceContour.RIGHT_EYE),
                    noseBridgeContour = it.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE),
                    noseBottomContour = it.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM),
                    upperLipTopContour = it.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP),
                    upperLipBottomContour = it.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM),
                    lowerLipTopContour = it.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP),
                    lowerLipBottomContour = it.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM),
                    leftEyeBrowTopContour = it.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP),
                    leftEyeBrowBottomContour = it.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM),
                    rightEyeBrowTopContour = it.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP),
                    rightEyeBrowBottomContour = it.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM),
                    smileProbability = it.smilingProbability
                    )
                contourFaces.add(contourFace)
            } else {
                val landmarkFace = LandmarkFace(
                    boundingBox = it.boundingBox,
                    leftEar = it.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR),
                    rightEar = it.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR),
                    leftEye = it.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE),
                    rightEye = it.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE),
                    leftMouth = it.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT),
                    rightMouth = it.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT),
                    bottomMouth = it.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM),
                    leftCheek = it.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK),
                    rightCheek = it.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK),
                    noseBase = it.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE),
                    smileProbability = it.smilingProbability
                    )
                landmarkFaces.add(landmarkFace)
            }
        }
        return drawFaceDetection(contourFaces, landmarkFaces)
    }

    private fun drawFaceDetection(contourFaces:List<ContourFace>, landmarkFaces: List<LandmarkFace>
    ): Bitmap {
        val bmp = Bitmap.createBitmap(cameraHeight, cameraWidth, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bmp)
        val paint = Paint()
        paint.strokeWidth = 4f

        contourFaces.forEach {
            with(canvas) {
                //paint.color = Color.argb(180, 0, 0, 0)
                //paint.style = Paint.Style.FILL
                //drawRect(it.boundingBox, paint)


                paint.color = Color.RED
                paint.style = Paint.Style.STROKE
                drawRect(it.boundingBox, paint)
                paint.style = Paint.Style.FILL
                paint.textSize=100f
                drawText(it.smileProbability().toString()+"\n", 300f, 100f, paint)
                //drawText(it.smileProbability().toString(), 300.0F,300.0F,paint)
                //drawText(it.smileProbability().toString(),it.boundingBox.left.toFloat(), it.boundingBox.top.toFloat(),paint)

                /*paint.style = Paint.Style.STROKE
                paint.color = Color.WHITE
                it.faceContourPoints().forEach { pts -> drawLines(pts, paint) }

                paint.color = Color.WHITE
                it.noseContourPoints().forEach { pts -> drawLines(pts, paint) }

                paint.color = Color.WHITE
                it.eyeContourPoints().forEach { pts -> drawLines(pts, paint) }

                paint.color = Color.WHITE
                it.lipContourPoints().forEach { pts -> drawLines(pts, paint) }

                paint.color = Color.WHITE
                it.eyeBrowContourPoints().forEach { pts -> drawLines(pts, paint) }
*/
            }
        }

        /*paint.style = Paint.Style.STROKE
        landmarkFaces.forEach {
            with(canvas) {
                paint.color = Color.WHITE
                drawRect(it.boundingBox, paint)
                drawPoints(it.earsPoints(), paint)
                drawPoints(it.cheekPoints(), paint)
                drawPoints(it.eyesPoints(), paint)
                drawPoints(it.mouthPoints(), paint)
                drawPoints(it.nosePoints(), paint)
            }
        }*/
        return bmp
    }
}

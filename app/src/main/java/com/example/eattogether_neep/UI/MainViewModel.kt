package com.example.eattogether_neep.UI

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eattogether_neep.domain.CameraManager
import com.example.eattogether_neep.domain.EmotionAnalyzer
import com.example.eattogether_neep.extension.*
import com.example.eattogether_neep.model.Emotion
import com.example.eattogether_neep.model.Face
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import org.tensorflow.lite.Interpreter
import timber.log.Timber
import kotlin.run

class MainViewModel(
    private val emotionAnalyzer: EmotionAnalyzer,
    private val cameraManager: CameraManager,
    private val emotionInterpreter: Interpreter
) : ViewModel() {

    val faces = MutableLiveData<List<Face>>()

    init {
        emotionAnalyzer.onFacesDetected(::onFaceDetected)
    }

    fun startCamera(activity: AppCompatActivity, cameraView: PreviewView) {
        cameraManager.startCamera(activity, cameraView, emotionAnalyzer)
    }

    private fun onFaceDetected(
        image: FirebaseVisionImage,
        firebaseFaces: List<FirebaseVisionFace>
    ) {
        val processedFaces = firebaseFaces.mapNotNull { face ->
            try {
                val bitmap = image.bitmap
                val faceRect = face.boundingBox
                processFace(bitmap, faceRect)
            } catch (exception: Exception) {
                Timber.e(exception)
                null
            }
        }

        faces.postValue(processedFaces)
    }

    private fun processFace(
        input: Bitmap,
        faceRect: Rect
    ): Face {
        val formattedFace = createMat(input)
            .crop(faceRect)
            .grayScale()
            .resize()

        val normalizedFace = formattedFace
            .normalize()
            .to4DArray()

        val result = emotionInterpreter.run(normalizedFace)

        return Face(
            emotion = Emotion.fromResult(result),
            bitmap = formattedFace.toBitmap(),
            imageRect = Rect(0, 0, input.width, input.height),
            faceRect = faceRect
        )
    }
}

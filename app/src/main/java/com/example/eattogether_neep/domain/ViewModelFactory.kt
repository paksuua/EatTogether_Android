package com.example.eattogether_neep.domain

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import org.tensorflow.lite.Interpreter
import com.example.eattogether_neep.R
import com.example.eattogether_neep.UI.MainViewModel
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executors

class ViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            MainViewModel::class.java -> createMainViewModel() as T
            else -> error("Unable to instantiate $modelClass")
        }
    }

    private fun createMainViewModel(): MainViewModel {
        val interpreter = application.resources
            .openRawResource(R.raw.model_facial_expression_quant)
            .use { stream ->
                val bytes = stream.readBytes()
                ByteBuffer.allocateDirect(bytes.size)
                    .order(ByteOrder.nativeOrder())
                    .put(bytes)
                    .let(::Interpreter)
            }

        val realTimeOpts = FirebaseVisionFaceDetectorOptions.Builder()
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .build()

        val detector = FirebaseVision
            .getInstance()
            .getVisionFaceDetector(realTimeOpts)

        val emotionAnalyzer =
            EmotionAnalyzer(detector)

        val cameraManager =
            CameraManager(
                ContextCompat.getMainExecutor(application),
                Executors.newSingleThreadExecutor()
            )

        return MainViewModel(
            emotionAnalyzer,
            cameraManager,
            interpreter
        )
    }
}

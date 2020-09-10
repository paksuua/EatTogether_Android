package com.example.eattogether_neep.domain

import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import com.google.common.util.concurrent.ListenableFuture
import timber.log.Timber
import java.util.concurrent.Executor

class CameraManager(
    private val mainExecutor: Executor,
    private val backgroundExecutor: Executor
) {

    fun startCamera(
        activity: AppCompatActivity,
        cameraView: PreviewView,
        analyzer: ImageAnalysis.Analyzer
    ) {
        ProcessCameraProvider.getInstance(activity).apply {
            addListener(
                cameraInitializer(activity, cameraView, analyzer),
                mainExecutor
            )
        }
    }

    private fun ListenableFuture<ProcessCameraProvider>.cameraInitializer(
        activity: AppCompatActivity,
        cameraView: PreviewView,
        analyzer: ImageAnalysis.Analyzer
    ) = Runnable {

        val preview = Preview.Builder()
            .build()
            .apply { setSurfaceProvider(cameraView.createSurfaceProvider()) }

        val imageAnalyzer = ImageAnalysis.Builder()
            .build()
            .apply { setAnalyzer(backgroundExecutor, analyzer) }

        try {
            val camera = get()
            camera.unbindAll()
            camera.bindToLifecycle(
                activity,
                CAMERA,
                preview,
                imageAnalyzer
            )
        } catch (exception: Exception) {
            Timber.e(exception)
        }
    }

    companion object {
        private val CAMERA = CameraSelector.DEFAULT_FRONT_CAMERA
    }
}

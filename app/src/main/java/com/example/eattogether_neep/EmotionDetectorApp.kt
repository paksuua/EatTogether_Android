package com.example.eattogether_neep

import android.app.Application
import com.google.firebase.FirebaseApp
import org.opencv.android.OpenCVLoader
import com.example.eattogether_neep.domain.ViewModelFactory

class EmotionDetectorApp : Application() {

    val viewModelFactory by lazy {
        ViewModelFactory(this)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(baseContext)
        OpenCVLoader.initDebug()
    }
}

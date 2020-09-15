package com.example.eattogether_neep.UI.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.eattogether_neep.R
import kotlinx.android.synthetic.main.activity_main.*

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
private const val REQUEST_CAMERA_PERMISSION = 123

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestCameraPermission()

        btn_make.setOnClickListener {
            val intent = Intent(this, MakeUrlActivity::class.java)
            startActivity(intent)
        }

        btn_join.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }
    }


    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            EmotionAnalysisActivity.REQUIRED_PERMISSIONS,
            EmotionAnalysisActivity.REQUEST_CODE_PERMISSIONS
        )
    }

    companion object {
        private const val TAG = "EmotionAnalysis"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CAMERA_PERMISSION = 123
        private var isFrontCamera = true
    }
}

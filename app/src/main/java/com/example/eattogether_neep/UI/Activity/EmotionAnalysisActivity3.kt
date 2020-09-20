package com.example.eattogether_neep.UI.Activity

import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.TextureView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.eattogether_neep.Network.EmotionDataRepository
import com.example.eattogether_neep.Network.Post.PostEmotionResponse
import com.example.eattogether_neep.R
import com.example.eattogether_neep.SOCKET.SocketService
import com.example.eattogether_neep.UI.User
import com.example.eattogether_neep.emotion.coredetection.DrawFace
import com.example.eattogether_neep.emotion.facedetection.FaceDetector
import kotlinx.android.synthetic.main.activity_emotion_analysis3.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread


typealias LumaListener3 = (luma: Double) -> Unit

private var mHandler: Handler? = null
private lateinit var socketReceiver: EmotionAnalysisActivity3.EmotionReciver3
private lateinit var intentFilter: IntentFilter
private var resultFromServer = -1

private var f_name: Array<String> = arrayOf()
private var f_img: Array<String> = arrayOf()
private  var imgOrder=0
private var photoCount:Int= 0
private var i=0
private var imgSuccessFlag=false


class EmotionAnalysisActivity3 : AppCompatActivity() {
    private lateinit var viewFinder: TextureView
    private lateinit var uuid: String
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var roomName = ""

    private val emotionDataRepository = EmotionDataRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_analysis3)

        f_name = intent.getStringArrayExtra("food_name")!!
        f_img = intent.getStringArrayExtra("food_img")!!
        roomName=intent.getStringExtra("roomName")!!
        Log.e("Food Name: ", f_name[0].toString())
        Log.e("Food Image: ", f_img[0].toString())

        checkPermission()
        startCameraThread()

        uuid = User.getUUID(this)
        socketReceiver = EmotionReciver3()
        intentFilter = IntentFilter()
        with(intentFilter){
            addAction("com.example.eattogether_neep.RESULT_SAVE_IMAGE")
            //addAction("com.example.eattogether_neep.RESULT_FINISH_PREDICT")
        }
        registerReceiver(socketReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(socketReceiver)
    }

    private fun startCameraThread(){
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        //camera_capture_button.setOnClickListener { takePhoto() }

        @SuppressLint("HandlerLeak")
        mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (this@EmotionAnalysisActivity3.isFinishing)
                    return
                if(i < f_img.size*3){

                    // Glide image load delay issue
                    Glide.with(this@EmotionAnalysisActivity3).load(f_img[i/3]).into(img_food)

                    tv_food_num.text="후보 "+(i/3+1)
                    txt_food_name.text = f_name[i / 3]

                    // 1초마다 표정, 기기번호, 음식번호 전송
                    i++
                    takePhoto()
                    Log.d("Image Index Atfter takePhoto","")
                    imgOrder=i/3
                }
                else {
                    val intent = Intent(
                        this@EmotionAnalysisActivity3,
                        WaitingReplyActivity::class.java
                    )
                    intent.putExtra("roomName", roomName)
                    Toast.makeText(this@EmotionAnalysisActivity3 ,"소켓안받음", Toast.LENGTH_SHORT).show()
                    this@EmotionAnalysisActivity3.startActivity(intent)
                    this@EmotionAnalysisActivity3.finish()
                    return
                }
            }
        }
        thread(start = true) {
            while (true) {
                Thread.sleep(1000)
                mHandler?.sendEmptyMessage(0)
            }
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory, roomName+"_"+uuid+"_"+imgOrder.toString()+"_"+
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    var photoPath = photoFile.canonicalPath
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d("onImageSaved", imgOrder.toString())
                    uploadImage(savedUri,roomName, uuid , imgOrder)
                    if (imgOrder>30){
                        imgSuccessFlag=!imgSuccessFlag
                    }
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }

    // 3초마다 이미지 uuid와 이미지 번호 전송
    private fun avgPredict(imageOrder: Int) {
        Log.d("Average Predict Called", "Emotion Analysis enqueue every 1seconds")
        val work = Intent()
        work.putExtra("serviceFlag", "avgPredict")
        work.putExtra("roomName", roomName)
        work.putExtra("uuid", uuid)
        work.putExtra("imageOrder", imageOrder)
        SocketService.enqueueWork(this, work)
    }

    private fun uploadImage(imageUri: Uri?, roomName:String ,uuid:String , imageCount:Int) {
        val roomName = uuid
        val uuid = uuid
        val imgOrder = (imageCount/3).toString()

        val roomNameR = RequestBody.create(MediaType.parse("text/plain"), roomName)
        val uuidR = RequestBody.create(MediaType.parse("text/plain"), uuid)
        val imgOrderR = RequestBody.create(MediaType.parse("text/plain"), imgOrder)

        val options = BitmapFactory.Options()
        val input = imageUri?.let { this.contentResolver.openInputStream(it) }
        val bitmap = BitmapFactory.decodeStream(input, null, options)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)

        val photoBody = RequestBody.create(
            MediaType.parse("image/jpg"),
            byteArrayOutputStream.toByteArray()
        )

        val pictureRb = MultipartBody.Part.createFormData(
            "img",
            File(imageUri.toString()).name,
            photoBody
        )
        Log.d("in upload image", imageUri.toString())

        emotionDataRepository
            .transferImage(
                pictureRb,
                roomNameR,
                uuidR,
                imgOrderR
            )
            .enqueue(object : Callback<PostEmotionResponse> {
                override fun onFailure(call: Call<PostEmotionResponse>, t: Throwable) {
                    Log.d("Emotion3 Upload Image","Fail to emotion rest 통신, message:${t.message}")
                }

                override fun onResponse(call: Call<PostEmotionResponse>, response: Response<PostEmotionResponse>) {
                    Log.d("Emotion3 Upload Image","emotion rest 통신 onResponse :${response.code()}")
                    if (response.isSuccessful) {
                        ++photoCount

                        if(photoCount%3==0) {
                            Log.d("여기서 avgPredict 호출", ((photoCount - 1) / 3).toString())

                            avgPredict((photoCount - 1) / 3)
                        }
                    }
                    // endregion
                }
            })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(
                    this,
                    "권한이 허용되지 않았습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cam_emotion3.createSurfaceProvider())
                }
            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.d(TAG, "Average luminosity: $luma")
                    })
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private class LuminosityAnalyzer(private val listener: LumaListener3) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name2)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this, CAMERA
            ) != PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION
            )
        }
    }

    inner class EmotionReciver3() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.eattogether_neep.RESULT_SAVE_IMAGE" -> {
                    resultFromServer = intent.getIntExtra("error", -1)
                    /*if (resultFromServer == 200) {
                        Log.d("EmotionActivity", "이미지 통신 성공")
                        //localJoin(edt_join_url.text.toString())
                    } else if (resultFromServer == 400) {
                        Toast.makeText(
                            this@EmotionAnalysisActivity,
                            "이미지 통신 실패.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }*/
                }
                /*"com.example.eattogether_neep.RESULT_FINISH_PREDICT" -> {
                    val intent = Intent(this@EmotionAnalysisActivity3, WaitingReplyActivity::class.java)
                    intent.putExtra("roomName", roomName)
                    Toast.makeText(this@EmotionAnalysisActivity3 ,"소켓받고죽음", Toast.LENGTH_SHORT).show()
                    this@EmotionAnalysisActivity3.startActivity(intent)
                    this@EmotionAnalysisActivity3.finish()
                }*/
                else -> return
            }
        }
    }

    companion object {
        private const val TAG = "EmotionAnalysis"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(CAMERA)
        private const val REQUEST_CAMERA_PERMISSION = 123
    }
}

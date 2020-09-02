package com.example.eattogether_neep.UI.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.*
import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.impl.PreviewConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.eattogether_neep.EmotionDetectorApp
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import com.example.eattogether_neep.R
import com.example.eattogether_neep.SOCKET.SocketService
import com.example.eattogether_neep.UI.MainViewModel
import com.example.eattogether_neep.UI.RectOverlay
import com.example.eattogether_neep.UI.User
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_emotion_analysis.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.http.Tag
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.lang.Thread.sleep
import java.net.Socket
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread


typealias LumaListener = (luma: Double) -> Unit

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
private val SOCKET_URL="[your server url]"

internal lateinit var preferences: SharedPreferences
private lateinit var food_img: ImageView
private lateinit var food_name: TextView

private var hasConnection: Boolean = false
private var mHandler: Handler? = null
private var mSocket: io.socket.client.Socket? = null
private lateinit var socketReceiver: EmotionAnalysisActivity.EmotionReciver
private lateinit var intentFilter: IntentFilter
private var resultFromServer = -1


private var f_name: Array<String> = arrayOf()
private var f_img: Array<String> = arrayOf()
private var base64Str:String=""
private var savedUri: Uri? =null


class EmotionAnalysisActivity : AppCompatActivity() {
    private lateinit var viewFinder: TextureView
    private  lateinit var uuid: String
    var images: Array<String> = arrayOf()
    var i=0
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

   /* private val viewModel: MainViewModel by viewModels {
        (application as EmotionDetectorApp).viewModelFactory
    }*/
    private val viewModel:MainViewModel by viewModels{
       (application as EmotionDetectorApp).viewModelFactory
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_analysis)

        f_name = intent.getStringArrayExtra("food_name")!!
        f_img = intent.getStringArrayExtra("food_img")!!
        /*if(f_name.isNullOrEmpty()){
            for (i in 0 until 10){
                f_name[i]="더미더미더미"
                f_img[i]="https://blog.naver.com/kym1903/221043545235"
            }
        }*/
        Log.e("Food Name: ", f_name[0].toString())
        Log.e("Food Image: ", f_img[0].toString())

        //saveImage()
        //avgPredict()

        startCameraThread()
        //setMenuImageThread(f_name, f_img)

        try {
            //IO.socket 메소드는 은 저 URL 을 토대로 클라이언트 객체를 Return 합니다.
            mSocket = IO.socket(SOCKET_URL)
        } catch (e: URISyntaxException) {
            Log.e("EmotionAnalysisActivity", e.reason)
        }

        uuid = User.getUUID(this)
        socketReceiver = EmotionReciver()
        intentFilter = IntentFilter()
        with(intentFilter){
            addAction("com.example.eattogether_neep.RESULT_SAVE_IMAGE")
        }
        registerReceiver(socketReceiver, intentFilter)

        //viewFinder = findViewById(R.id.cam_emotion)

        /*if (allPermissionsGranted()) {
            viewFinder.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }*/

    }

    override fun onStart(){
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(socketReceiver)
    }

    private fun startCameraThread(){
        // Request camera permissions
        if (allPermissionsGranted()) {

            // 이거 둘 중 하나만 써야하려나..?
            viewModel.startCamera(this, cam_emotion)
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        viewModel.faces.observe(
            this,
            androidx.lifecycle.Observer { faces ->
                if (faces != null){
                    emotion.faces=faces
                }
            }
        )

        // Set up the listener for take photo button
        //camera_capture_button.setOnClickListener { takePhoto() }

        @SuppressLint("HandlerLeak")
        mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                Glide.with(this@EmotionAnalysisActivity).load(f_img[i/3]).into(img_food)
                tv_food_num.text="후보 "+(i/3+1)
                txt_food_name.text = f_name[i/3]
                i++


                // 1초마다 표정, 기기번호, 음식번호 전송
                takePhoto()
                Log.d("1초마다 표정, 기기번호, 음식번호 전송", "Emotion Analysis enqueue every 1seconds")
                saveImage(i/3, encoder2(savedUri))

                // 3초마다 기기번호, 음식번호
                if(i%3==0){
                    Log.d("3초마다 기기번호, 음식번호","Emotion Analysis enqueue every 3seconds")
                    avgPredict(i/3)
                }

                if((i/3)>= f_name.size) {
                    val intent = Intent(this@EmotionAnalysisActivity, RankingActivity::class.java)
                    startActivity(intent)
                    //finish()
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



    // 3초마다 하단에 메뉴와 이미지 보내줌
    private fun setMenuImageThread(f_name: Array<String>, f_img: Array<String>){
        @SuppressLint("HandlerLeak")
        mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val cal = Calendar.getInstance()

               /* val sdf = SimpleDateFormat("HH:mm:ss")
                val strTime = sdf.format(cal.time)*/

               /* Glide.with(this@EmotionAnalysisActivity).load(f_img[i%3]).into(img_food)
                tv_food_num.text="후보 "+i%3
                txt_food_name.text = f_name[i%3]
                i++*/

                // 1초마다 표정, 기기번호, 음식번호 전송
                //Log.d("1초마다 표정, 기기번호, 음식번호 전송", "Emotion Analysis enqueue every 1seconds")
                //saveImage(i%3)

                /*// 3초마다 기기번호, 음식번호
                if(i%3==0){
                    Log.d("SaveImage Called:",image64)
                    avgPredict(i%3)
                }

                Log.d("3Second Thread","View every 3seconds")
                if((i%3)>= f_name.size) {
                    val intent = Intent(this@EmotionAnalysisActivity, RankingActivity::class.java)
                    startActivity(intent)
                }*/
            }
        }

        thread(start = true) {
            while (true) {
                sleep(1000)
                mHandler?.sendEmptyMessage(0)
            }
        }
    }

    private fun takePhoto() {
        //setUpCameraOutputsFront()
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
       val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")
        /*photoFile=File(
            Environment.getExternalStorageDirectory(),SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")*/

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile!!).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    var photoPath = photoFile.canonicalPath
                    savedUri = Uri.fromFile(photoFile)
                    //val savedUri = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/4/41/Sunflower_from_Silesia2.jpg")//Uri.fromFile(photoFile)
                    Log.d("Before Base64 encoder",savedUri.toString())
                    Log.d("Base64 encoder111", encoder1(photoFile))
                    Log.d("Base64 encoder222", encoder2(savedUri))
                    //base64Str=encoder2(saveUri)
                    //saveImage(imageOrder, base64Str)

                    Log.d("Base64 encoder333", encoder3(photoPath))
                    //val result=decoder(encoder(photoFile.toString()), photoFile.toString())
                    //Log.d("Base64 decoded", result.toString())

                    //encoder2(savedUri)

                    //val msg = "Photo capture succeeded: $savedUri"
                    //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    //Log.d(TAG, msg)
                }
            })
    }

    // Convert Failed to Image
    private fun encoder1(filePath: File): String{
        val bytes = filePath.readBytes()
        val base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
        return base64
    }


    // Saved Broken Image
    private fun encoder2(imageUri: Uri?): String {
        val input = imageUri?.let { this.contentResolver.openInputStream(it) }
        //val bm = BitmapFactory.decodeResource(resources, R.drawable.test)
        val image = BitmapFactory.decodeStream(input, null, null)
        //encode image to base64 string
        val baos = ByteArrayOutputStream()
        //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        image!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var imageBytes = baos.toByteArray()

        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_WRAP)
        //return Base64.getEncoder().encodeToString(imageBytes) // Not Worked, too.
    }

    // Saved Broken Image
    private fun encoder3(path: String): String {
        val imagefile = File(path)
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(imagefile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val bm = BitmapFactory.decodeStream(fis)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()

        return Base64.getEncoder().encodeToString(b)
    }

    fun decoder(base64Str: String, pathFile: String): Unit{
        val imageByteArray = Base64.getDecoder().decode(base64Str)
        File(pathFile).writeBytes(imageByteArray)
    }


    private fun saveImage(imageOrder: Int, base64Str: String) {
        Log.d("SaveImage Called", "")
        val work = Intent()
        var image_666="Dummy Base64 Code"
        //var image64=encoder(photoFile.toString())
        var foodImage22="file:///storage/emulated/0/Android/media/com.soyeon.cameraxtutorial/CameraX%20Tutorial/2020-08-28-21-24-24-668.jpg"
        val encodedURL = Base64.getUrlEncoder().encodeToString(foodImage22.toByteArray())
        //var image64=foodImage22.base64decoded
        //encoder

        ///val encodeString=encoder("src/main/res/drawable/neww.JPG")
        Log.d("SaveImage Called:",base64Str)
        work.putExtra("serviceFlag", "saveImage")
        work.putExtra("image", base64Str)
        work.putExtra("uuid", uuid)
        work.putExtra("imageOrder", imageOrder)
        SocketService.enqueueWork(this, work)
    }

    private fun avgPredict(imageOrder:Int) {
        Log.d("Average Predict Called", "Emotion Analysis enqueue every 1seconds")
        val work = Intent()
        work.putExtra("serviceFlag", "avgPredict")
        work.putExtra("uuid", uuid)
        work.putExtra("imageOrder", imageOrder)
        SocketService.enqueueWork(this, work)
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
                Toast.makeText(this,
                    "권한이 허용되지 않았습니다.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
            return
        }
        if (allPermissionsGranted()) {
            viewModel.startCamera(this, cam_emotion)
        } else {
            Toast.makeText(this, "Missing camera permission.", Toast.LENGTH_SHORT).show()
            finish()
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
                    it.setSurfaceProvider(cam_emotion.createSurfaceProvider())
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
                    this, cameraSelector, preview,  imageCapture, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

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
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    // firebase
    private fun runDetector(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .build()

        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(options)

        detector.detectInImage(image)
            .addOnSuccessListener { faces ->
                processFaceResult(faces)

            }.addOnFailureListener {
                it.printStackTrace()
            }

    }
    private fun processFaceResult(faces: MutableList<FirebaseVisionFace>) {
        faces.forEach {
            val bounds = it.boundingBox
            val rectOverLay = RectOverlay(graphic_overlay, bounds)
            graphic_overlay.add(rectOverLay)
        }
    }

    inner class EmotionReciver() : BroadcastReceiver() {
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
                else -> return
            }
        }
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private var isFrontCamera = true
    }
}

package com.example.eattogether_neep.UI.Activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import com.example.eattogether_neep.Data.FoodItem
import com.example.eattogether_neep.Data.MenuItem
import com.example.eattogether_neep.R
import com.example.eattogether_neep.SOCKET.SocketService
import com.example.eattogether_neep.UI.RectOverlay
import com.example.eattogether_neep.UI.User
import com.example.myapplication.base64decoded
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.android.synthetic.main.activity_emotion_analysis.*
import java.io.File
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
internal lateinit var preferences: SharedPreferences
private lateinit var food_img: ImageView
private lateinit var food_name: TextView
private var imageOrder="-1"
private var roomName = "961219"
private var resultFromServer = -1
private var like = ""
private var hate = ""
//private var foodList:ArrayList<MenuItem> = ArrayList()
private var foodList:ArrayList<String> = ArrayList()
private var hasConnection: Boolean = false
private var mSocket: io.socket.client.Socket? = null
private lateinit var socketReceiver: EmotionAnalysisActivity.EmotionReciver
private lateinit var intentFilter: IntentFilter

class EmotionAnalysisActivity : AppCompatActivity() {
    private lateinit var viewFinder: TextureView
    private lateinit var uuid: String
    var foodList=ArrayList<String>()
    var images: Array<String> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_analysis)

        // Waiting 에서 foodlist 받아옴
        foodList = intent.getStringArrayListExtra("foodlist")
        if(foodList.isNullOrEmpty()){
            foodList[0]="11111111"
            foodList[1]="22222222"
            foodList[2]="33333333"
            foodList[3]="11111111"
            foodList[4]="22222222"
            foodList[5]="33333333"
            foodList[6]="11111111"
            foodList[7]="22222222"
            foodList[8]="33333333"
            foodList[9]="33333333"
        }

        uuid=User.getUUID(this)
        socketReceiver = EmotionReciver()
        intentFilter = IntentFilter()
        with(intentFilter){
            addAction("com.example.eattogether_neep.RESULT_SAVE_IMAGE")
        }
        registerReceiver(socketReceiver, intentFilter)
        
        // 3초마다 메뉴 이미지 보여줌
        setMenuImage()


        val imagePath = "C:\\base64\\image.png"
        val base64ImageString = encoder(imagePath)

        /*try {
            //IO.socket 메소드는 은 저 URL 을 토대로 클라이언트 객체를 Return 합니다.
            mSocket = IO.socket(SOCKET_URL)
        } catch (e: URISyntaxException) {
            Log.e("EmotionAnalysisActivity2", e.reason)
        }

        uuid = User.getUUID(this)*/

        viewFinder = findViewById(R.id.cam_emotion)

        if (allPermissionsGranted()) {
            viewFinder.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }
    }

    override fun onStart() {
        super.onStart()

        setMenuImage()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(socketReceiver)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1]==PackageManager.PERMISSION_GRANTED) {
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

    // 3초마다 하단에 메뉴와 이미지 보내줌
    private fun setMenuImage(){
        permissionCheck()

        val handler = Handler()
        val millisTime=3000
        val handlerTask = object : Runnable {
            override fun run() {
                // 3초마다 보냄
                saveImage()

                handler.postDelayed(this, millisTime.toLong()) // millisTiem 이후 다시
            }
        }

        handlerTask.run()
    }

    // 1초마다 표정 base64 보여줌
    private fun setFaceImage(){
        permissionCheck()

        val handler = Handler()
        val millisTime=1000

        val handlerTask = object : Runnable {
            override fun run() {
                // 1초마다 보냄
                saveFaceImage()

                handler.postDelayed(this, millisTime.toLong()) // millisTiem 이후 다시
            }
        }
    }

    private fun permissionCheck(){
        var cameraPermission: Int= ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        var writeExternalStoragePermisson: Int= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (cameraPermission==PackageManager.PERMISSION_GRANTED && writeExternalStoragePermisson==PackageManager.PERMISSION_GRANTED){

            //setupCamera()
        }else{
            //ActivityCompat.requestPermissions(this, arrayPermisson, requestPermission)
        }
    }

    /*@Override
    override fun onRequestPermissionsResult(requestCode: Int, permissions:Array<out String>, grantResults: IntArray){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode==requestPermission &&grantResults[0]==PackageManager.PERMISSION_GRANTED
            && grantResults[1]==PackageManager.PERMISSION_GRANTED){
            setupCamera()
        }else{
            Toast.makeText(this, "permisson ar not granted", Toast.LENGTH_LONG).show()
        }
    }*/

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
        private var lastAnalyzedTimestamp = 0L

        /**
         * 이미지 버퍼를 바이트 배열로 추출하기 위한 익스텐션
         */
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // 버퍼의 포지션을 0으로 되돌림
            val data = ByteArray(remaining())
            get(data)   // 바이트 버퍼를 바이트 배열로 복사함
            return data // 바이트 배열 반환함
        }

        override fun analyze(image: ImageProxy, rotationDegrees: Int) {
            val currentTimestamp = System.currentTimeMillis()
            // 매프레임을 계산하진 않고 1초마다 한번씩 정도 계산
            if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {
                // 이미지 포맷이 YUV이므로 image.planes[0]으로 Y값을 구할수 있다.
                val buffer = image.planes[0].buffer
                // 이미지 데이터를 바이트배열로 추출
                val data: ByteArray = buffer.toByteArray()
                // 픽셀 하나하나를 유의미한 데이터리스트로 만든다
                val pixels: List<Int> = data.map { it.toInt() and 0xFF }
                // 바이트 배열을 Base64로 매핑
                val base64 = Base64.getEncoder().encodeToString(data)

           /* val byteArrayOutputStream = image.image.toString()
            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
            val encodedByteArrayString: String = Base64.getEncoder().encode(data).toString()*/
           // base64.base64decoded

                // socket 통신으로 보내기
                //sendIMG(pixels)

                // 이미지의 평균 휘도를 구한다
                val luma: Double = pixels.average()
                // 로그에 휘도 출력
                Log.d("우리 뭐 먹지", "Average luminosity: $luma")
                // 로그로 data 어떻게 찍히는지 확인하기!
                Log.d("우리 뭐 먹지", "Image Buffer: $buffer")
                Log.d("우리 뭐 먹지", "Image ByteArray: $data")
                Log.d("우리 뭐 먹지", "Image Pixels: $pixels")
                Log.d("우리 뭐 먹지", "Image Base64 Data: $base64")

                // 마지막 분석한 프레임의 타임스탬프로 업데이트한다.
                lastAnalyzedTimestamp = currentTimestamp
            }
        }


        /*fun sendIMG(pixels:List<Int>) {
            val now = System.currentTimeMillis()
            val date = Date(now)
            //나중에 바꿔줄것
            val sdf = SimpleDateFormat("yyyy-MM-dd")

            val getTime = sdf.format(date)

            val jsonObject = JSONObject()
            try {
                jsonObject.put("name", preferences.getString("name", ""))
                //byte 전송
                jsonObject.put("cameraview", pixels)
                jsonObject.put("date_time", getTime)
                jsonObject.put("roomNum", "room_example")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Log.e("챗룸", "sendMessage: 1" + mSocket?.emit("chat message", jsonObject))
            Log.e("sendmmm",preferences.getString("name", "") )

        }*/

    }

    private fun startCamera() {
        //미리보기 설정 시작
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetAspectRatio(Rational(1, 1))
            setTargetResolution(Size(viewFinder.width, viewFinder.height))
        }.build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            viewFinder.surfaceTexture = it.surfaceTexture
            parent.addView(viewFinder, 0)
            updateTransform()
        }
        //미리보기 설정 끝

        //이미지 프로세싱 설정 시작
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            // 이미지 분석을 위한 쓰레드를 하나 생성합니다.
            val analyzerThread = HandlerThread("LuminosityAnalysis").apply { start() }
            setCallbackHandler(Handler(analyzerThread.looper))
            // 하나도 빠짐없이 프레임 전부를 분석하기보다는 매순간 가장 최근 프레임만을 가져와 분석하도록 합니다
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE) }.build()

        // 커스텀 이미지 프로세싱 객체 생성
        val analyzerUseCase = ImageAnalysis(analyzerConfig).apply {
            analyzer = LuminosityAnalyzer()
        }
        //이미지 프로세싱 설정 끝

        //유즈케이스들을 바인딩함 // 여기서 에러날때는 보통 analysis 확인해볼 것
        CameraX.bindToLifecycle(this, preview, analyzerUseCase)
    }

    //private

    /*private fun setupCamera(){
        if(mCamera==null){
            mCamera=Camera.open()
        }

        cameraPreview=CameraPreview(this,mCamear!!)
        camera_frameLayout.addView(cameraPreview)
    }*/



    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        viewFinder.setTransform(matrix)
    }

   /* private setDummyFoodList(){
        foodList.apply {
            add(
                MenuItem(
                    name = "달걀볶음밥",
                    image = "https://blog.naver.com/skduskong/220046366458"
                ),
                MenuItem(
                    name = "보쌈",
                    image = "https://blog.naver.com/kym1903/221043545235"
                ),
                MenuItem(
                    name = "돈까스",
                    image = "https://blog.naver.com/kym1903/221043545235"
                )
        }
    }*/

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

    //Encode picture to base64
    fun encoder(filePath: String): String {
        val bytes = File(filePath).readBytes()
        val base64 = Base64.getEncoder().encodeToString(bytes)
        return base64
    }

    private fun saveImage() {
        val work = Intent()
        var image_666="Emotion Analysis enqueue every 3seconds"
        var image64=image_666.base64decoded
        work.putExtra("img", image64)
        work.putExtra("deviceNum", uuid)
        work.putExtra("imgOrder", 1)
        SocketService.enqueueWork(this, work)
    }

    private fun saveFaceImage() {
        val work = Intent()
        var image_666="EmotinoAnalysis image64 String"
        var image64=image_666.base64decoded
        work.putExtra("deviceNum", uuid)
        work.putExtra("imgOrder", imageOrder)
        SocketService.enqueueWork(this, work)
    }


    inner class EmotionReciver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.eattogether_neep.RESULT_SAVE_IMAGE" -> {
                    resultFromServer = intent.getIntExtra("result", -1)
                    if (resultFromServer == 200) {
                        Log.d("EmotionActivity", "이미지 통신 성공")
                        //localJoin(edt_join_url.text.toString())
                    } else if (resultFromServer == 400) {
                        Toast.makeText(
                            this@EmotionAnalysisActivity,
                            "이미지 통신 실패.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else -> return
            }
        }
    }
}

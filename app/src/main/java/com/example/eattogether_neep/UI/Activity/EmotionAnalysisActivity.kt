package com.example.eattogether_neep.UI.Activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Base64.*
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
import com.bumptech.glide.Glide
import com.example.eattogether_neep.R
import com.example.eattogether_neep.SOCKET.SocketService
import com.example.eattogether_neep.UI.RectOverlay
import com.example.eattogether_neep.UI.User
import com.google.android.gms.common.util.IOUtils.toByteArray
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_emotion_analysis.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
private val SOCKET_URL="[your server url]"

internal lateinit var preferences: SharedPreferences
private lateinit var food_img: ImageView
private lateinit var food_name: TextView
private var roomName = "961219"
private var like = ""
private var hate = ""
//private var foodList:ArrayList<MenuItem> = ArrayList()
private var foodList:ArrayList<String> = ArrayList()
private var hasConnection: Boolean = false
private var mSocket: io.socket.client.Socket? = null
private lateinit var socketReceiver: EmotionAnalysisActivity.MenuReciver
private lateinit var intentFilter: IntentFilter

class EmotionAnalysisActivity : AppCompatActivity() {
    private lateinit var viewFinder: TextureView
    private lateinit var uuid: String
    var images: Array<String> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_analysis)

        //roomName = intent.getStringExtra("roomName")
        uuid=User.getUUID(this)
        socketReceiver = MenuReciver()
        intentFilter = IntentFilter()
        with(intentFilter){
            addAction("com.example.eattogether_neep.RESULT_OPPONENT_INFO2")
            addAction("com.example.eattogether_neep.RESULT_ROOM_NUM2")
            addAction("com.example.eattogether_neep.RESULT_FOOD_LIST")
        }
        registerReceiver(socketReceiver, intentFilter)

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

        /*preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)

        food_img = findViewById(R.id.img_food)
        food_name = findViewById(R.id.txt_food_name)
        if (savedInstanceState != null) {
            hasConnection = savedInstanceState.getBoolean("hasConnection")
        }
        if (hasConnection) {
        } else {
            //소켓연결
            mSocket.connect()
            //서버에 신호 보내는거같음 밑에 에밋 리스너들 실행
            //socket.on은 수신
            //mSocket.on("connect user", onNewUser)
            mSocket.on("chat message", onNewMessage)

            val userId = JSONObject()
            try {
                userId.put("username", preferences.getString("name", "") + " Connected")
                userId.put("roomNum", "room_example")
                Log.e("username",preferences.getString("name", "") + " Connected")

                //socket.emit은 메세지 전송임
                mSocket.emit("connect user", userId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        hasConnection = true*/
    }

    override fun onStart() {
        super.onStart()
        saveImage()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(socketReceiver)
    }

    /*internal var onNewMessage: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val f_name: String
            val f_img: String
            try {
                Log.e("asdasd", data.toString())
                f_name = data.getString("name")
                f_img = data.getString("profile_image")

                //서버쪽으로 uuid 과 함께 보냄
                mSocket?.emit("login", uuid)
                Log.d("", "Socket is connected with ${uuid}")

                Glide.with(this).load(f_img).into(img_food)
                txt_food_name.setText(f_name)
                Log.e("new me", f_name)
            } catch (e: Exception) {
                return@Runnable
            }
        })
    }*/

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

        /*override fun analyze(image: ImageProxy, rotationDegrees: Int) {
            val currentTimestamp = System.currentTimeMillis()
            // 매프레임을 계산하진 않고 1초마다 한번씩 정도 계산
            if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {
                // 이미지 포맷이 YUV이므로 image.planes[0]으로 Y값을 구할수 있다.
                val buffer = image.planes[0].buffer
                // 이미지 데이터를 바이트배열로 추출
                val data:ByteArray = buffer.toByteArray()
                // 픽셀 하나하나를 유의미한 데이터리스트로 만든다
                val pixels:List<Int> = data.map { it.toInt() and 0xFF }
                // 이미지의 평균 휘도를 구한다
                val luma:Double = pixels.average()
                // 로그에 휘도 출력
                Log.d("우리 뭐 먹지", "Average luminosity: $luma")
                // 마지막 분석한 프레임의 타임스탬프로 업데이트한다.
                lastAnalyzedTimestamp = currentTimestamp
            }
        }*/

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
        var image64="2323423254235"
        work.putExtra("img", image64)
        work.putExtra("deviceNum", uuid)
        work.putExtra("imgOrder", 1)
        SocketService.enqueueWork(this, work)
    }


    inner class MenuReciver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.eattogether_neep.RESULT_OPPONENT_INFO2" -> {
                    roomName = intent.getStringExtra("roomName")!!

                    val intent = Intent(this@EmotionAnalysisActivity, RankingActivity::class.java)
                    with(intent) {
                        putExtra("roomName", roomName)
                    }
                }
                "com.example.eattogether_neep.RESULT_ROOM_NUM2" -> roomName =
                    intent.getStringExtra("roomName")!!

                "com.example.eattogether_neep.RESULT_FOOD_LIST" -> foodList =
                    intent.getStringArrayListExtra("foodList")

                "com.example.eattogether_neep.RESULT_ERROR" -> {
                    //error= intent.getIntExtra("suc", -1)
                    /*if(suc == 0) {
                        Log.d("enter","success")
                        //localJoin(edt_join_url.text.toString())
                    }*/
                }
                else -> return
            }
        }
    }
}

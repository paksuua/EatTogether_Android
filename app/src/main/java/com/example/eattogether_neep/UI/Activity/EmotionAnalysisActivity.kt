package com.example.eattogether_neep.UI.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit
import com.example.eattogether_neep.R
import com.example.eattogether_neep.SOCKET.SocketService
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
import java.io.File
import java.lang.Thread.sleep
import java.net.Socket
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask
import kotlin.system.exitProcess

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


class EmotionAnalysisActivity : AppCompatActivity() {
    private lateinit var viewFinder: TextureView
    private  lateinit var uuid: String
    var images: Array<String> = arrayOf()
    var i=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_analysis)

        f_name = intent.getStringArrayExtra("food_name")!!
        f_img = intent.getStringArrayExtra("food_img")!!
        Log.e("Food Name: ", f_name[0].toString())
        Log.e("Food Image: ", f_img[0].toString())

        //avgPredict()
        //saveImage()

        //setMenuImage(f_name, f_img)
        @SuppressLint("HandlerLeak")
        mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                Glide.with(this@EmotionAnalysisActivity).load(f_img[i]).into(img_food)
                val cal = Calendar.getInstance()

                val sdf = SimpleDateFormat("HH:mm:ss")
                val strTime = sdf.format(cal.time)
                tv_food_num.text="후보 "+i
                txt_food_name.text = f_name[i]
                i++

                Log.d("3Second Thread","View every 3seconds")
                if(i>= f_name.size) {
                    val intent = Intent(this@EmotionAnalysisActivity, RankingActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        thread(start = true) {
            while (true) {
                sleep(3000)
                mHandler?.sendEmptyMessage(0)
            }
        }



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

    override fun onStart(){
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
    override fun onDestroy() {
        super.onDestroy()
    }



    // 3초마다 하단에 메뉴와 이미지 보내줌
    private fun setMenuImage(f_name: Array<String>, f_img: Array<String>){
        var flag=true

        /*@SuppressLint("HandlerLeak")
        mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {

                for(i in 0 until (f_name.size)) {
                    Glide.with(this@EmotionAnalysisActivity).load(f_img[i]).into(img_food)
                    txt_food_name.text = f_name[i]
                    Log.d("3Second Thread","View every 3seconds")
                }
            }
        }

        thread(start = true) {
            while (true) {
                sleep(3000)
                mHandler?.sendEmptyMessage(0)
            }
        }*/

        /*val tt= object: TimerTask(){
            override fun run() {

                for(i in 0 until (f_name.size)){
                    // UI update를 위한 스레드
                    runOnUiThread{

                        Glide.with(this@EmotionAnalysisActivity).load(f_img[i]).into(img_food)
                        txt_food_name.text = f_name[i]
                        sleep(3000)


                        // 내가 보낸 메시지인지, 받은 메시지인지 구분하기 위한 조건
                        if(receiveMessage.getString("name").toString() != nickName){
                            chatAdapter.addItem(ChatData(
                                "you",
                                receiveMessage.getString("message").toString(),
                                receiveMessage.getString("name").toString(),
                                "https://images.otwojob.com/product/x/U/6/xU6PzuxMzIFfSQ9.jpg/o2j/resize/852x622%3E",
                                ""))
                            chatAdapter.notifyDataSetChanged()
                            rv_chatact_chatlist.scrollToPosition(rv_chatact_chatlist.adapter!!.itemCount - 1)
                        }
                    }
                }
            }
        }
        tt.run()*/
    }


    private fun saveImage() {
        Log.d("SaveImage Called", "")
        val work = Intent()
        var image_666="Emotion Analysis enqueue every 3seconds"
        //var image64=image_666.base64decoded
        var foodImage22="https://cbmpress.sfo2.digitaloceanspaces.com/tfood/2516102861_Dou6sN2f_83893dfb9a46cdd27c7d3d51eff246dc766b2dc8.jpg"
        //val encodedURL = Base64.getUrlEncoder().encodeToString(foodImage22.toByteArray())
        //var image64=foodImage22.base64decoded
        //encoder

        ///val encodeString=encoder("src/main/res/drawable/neww.JPG")
        Log.d("SaveImage Called:",image_666)
        work.putExtra("serviceFlag", "saveImage")
        work.putExtra("image", image_666)
        work.putExtra("uuid", uuid)
        work.putExtra("imageOrder", "1")
        SocketService.enqueueWork(this, work)
    }

    private fun avgPredict(imageOrder:Int) {
        Log.d("Average Predict Called", "Emotion Analysis enqueue every 1seconds")
        val work = Intent()
        work.putExtra("serviceFlag", "avgPredict")
        work.putExtra("uuid", uuid)
        work.putExtra("imageOrder", 1)
        SocketService.enqueueWork(this, work)
    }

    //Encode picture to base64
    fun encoder(filePath: String): String {
        val bytes = File(filePath).readBytes()
        val base64 = Base64.getEncoder().encodeToString(bytes)
        return base64
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(this,
                    "권한이 허용되지 않았습니다.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, permission) != PackageManager.PERMISSION_GRANTED) {
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
                val data:ByteArray = buffer.toByteArray()
                // 픽셀 하나하나를 유의미한 데이터리스트로 만든다
                val pixels:List<Int> = data.map { it.toInt() and 0xFF }
                // 바이트 배열을 Base64로 매핑
                val base64=Base64.getEncoder().encodeToString(data)

                // socket 통신으로 보내기
                //sendIMG(pixels)

                // 이미지의 평균 휘도를 구한다
                val luma:Double = pixels.average()
                // 로그에 휘도 출력
                //Log.d("우리 뭐 먹지", "Average luminosity: $luma")
                // 로그로 data 어떻게 찍히는지 확인하기!
                //Log.d("우리 뭐 먹지", "Image Base64 Data: $base64")

                // 마지막 분석한 프레임의 타임스탬프로 업데이트한다.
                lastAnalyzedTimestamp = currentTimestamp
            }
        }
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
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

        // 커스텀 이미지 프로세싱 객체 생성
        val analyzerUseCase = ImageAnalysis(analyzerConfig).apply {
            analyzer = LuminosityAnalyzer()
        }
        //이미지 프로세싱 설정 끝

        //유즈케이스들을 바인딩함
        CameraX.bindToLifecycle(this, preview, analyzerUseCase)
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(viewFinder.display.rotation) {
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
}

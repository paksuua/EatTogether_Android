package com.example.eattogether_neep.socket

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import io.socket.client.Socket
import java.net.URISyntaxException

class SocketService : JobIntentService() {
    private val TAG = SocketService::class.simpleName
    private var mSocket: Socket = SingleSocket.getInstance(this@SocketService)
    private var isSocketExist = false
    private val mHost = "http://13.125.224.168:8000/ranking"
    private var roomName: String = ""


    override fun onHandleWork(intent: Intent) {
        if (!isSocketExist) {
            try {
//                socketConnect()
            } catch (e: URISyntaxException) {
                Log.d(
                    TAG,
                    "Socket Connect Reason: ${e.reason} (index: ${e.index}) (message:${e.message}"
                )
            }
            isSocketExist = true
        }
        //"Appear on onHandleWork".logDebug(this@SocketService)
        when (intent.getStringExtra("serviceFlag")) {
            "createRoom" -> {
                val roomName = intent.getStringExtra("roomName")
                val uuid = intent.getStringExtra("uuid")

                mSocket.emit("createRoom", roomName, uuid)
            }
            "preference" -> {
                val like = intent.getStringExtra("like")
                val hate = intent.getStringExtra("hate")
                val uuid = intent.getStringExtra("uuid")
                val roomName = intent.getStringExtra("roomName")

                mSocket.emit("preference", like, hate, uuid, roomName)
            }
            "preference" -> {
                val like = intent.getStringExtra("like")
                val hate = intent.getStringExtra("hate")
                val uuid = intent.getStringExtra("uuid")
                val roomName = intent.getStringExtra("roomName")

                mSocket.emit("preference", like, hate, uuid, roomName)
            }
            "saveImage" -> {
                val img = intent.getStringExtra("img")
                val uuid = intent.getStringExtra("uuid")
                val imageOrder = intent.getStringExtra("imageOrder")

                mSocket.emit("saveImage", img, uuid, imageOrder)
            }
            // 평균 happiness 전송
            "savePredict" -> {
                val avgHappy=intent.getFloatExtra("avgHappy", 0f)
                val avgNeutral=intent.getFloatExtra("avgNeutral", 0f)
                val uuid = intent.getStringExtra("uuid")
                val imageOrder = intent.getIntExtra("imageOrder", -1)

                mSocket.emit("savePredict", avgHappy,avgNeutral, uuid, imageOrder)
            }
            
            "avgPredict" -> {
                Log.d("AvgPredict Called in SocketService.","")
                val roomName = intent.getStringExtra("roomName")
                val uuid = intent.getStringExtra("uuid")
                val imageOrder =intent.getIntExtra("imageOrder", -1)

                mSocket.emit("avgPredict", roomName, uuid, imageOrder)
            }
            "ping" -> {
                Log.d("Ping Called in SocketService.","")
                val roomName = intent.getStringExtra("roomName")

                mSocket.emit("ping", roomName)
            }
            "showRank" -> {
                val roomName = intent.getStringExtra("roomName")

                mSocket.emit("showRank", roomName)
            }
            "finishRank" ->{
                val roomName = intent.getStringExtra("roomName")

                mSocket.emit("finishRank", roomName)
            }
        }
    }

    companion object {
        const val JOB_ID = 812057698
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, SocketService::class.java,
                JOB_ID, work)
        }
    }
}

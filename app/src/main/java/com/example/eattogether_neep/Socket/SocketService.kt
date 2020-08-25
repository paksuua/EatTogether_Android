package com.example.eattogether_neep.SOCKET

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import androidx.core.app.JobIntentService
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
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
            "saveImage" -> {
                val image = intent.getStringExtra("image")
                val uuid = intent.getStringExtra("uuid")
                val imageNum = intent.getStringExtra("imageOrder")

                mSocket.emit("saveImage", image, uuid, imageNum)
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

package com.example.eattogether_neep.Socket

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.util.DebugUtils
import io.socket.client.Socket
import java.net.URISyntaxException

class SocketService: JobIntentService(){
    private val TAG = SocketService::class.simpleName
    private var mSocket: Socket = SingleSocket.getInstance(this@SocketService)
    private var isSocketExist = false
    private val mHost = "http://13.125.224.168:8000/ranking"
    private var roomName: String = ""

    override fun onHandleWork(intent: Intent) {
        if (!isSocketExist) {
            try {
                // socketConnect()
            } catch (e: URISyntaxException) {
                Log.d(
                    TAG,
                    "Socket Connect Reason: ${e.reason} (index: ${e.index}) (message:${e.message}"
                )
            }
            isSocketExist = true
        }
        //"Appear on onHandleWork".logDebug(this@SocketService)
        when (intent.getStringExtra("serviceFlag")) { // serviceFlag
            "createRoom" -> { // joinRoom
                val roomID = intent.getStringExtra("roomID")
                val deviceNum = intent.getStringExtra("deviceNum")
                /*"JoinRoom (roomID: $roomID) (deviceNum: $deviceNum) (SocketId: ${mSocket.id()})".logDebug(
                    this@SocketService)*/

                mSocket.emit("createRoom", roomID, deviceNum)
                //"Send JoinRoom".logDebug(this@SocketService)
            }

            "result" -> {
                val flag_success=intent.getIntExtra("flag_success", -1)
                val flag_fail=intent.getIntExtra("flag_fail", -1)
                mSocket.emit("result", flag_success, flag_fail)
            }
        }
    }

    companion object {
        const val JOB_ID = 1001
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, SocketService::class.java,
                JOB_ID, work)
            Log.d(this.toString(), "SocketService companion object")
        }
    }
}
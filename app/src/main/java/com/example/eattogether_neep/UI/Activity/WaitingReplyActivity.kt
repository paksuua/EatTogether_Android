package com.example.eattogether_neep.UI.Activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.example.eattogether_neep.R
import com.example.eattogether_neep.SOCKET.SocketService
import com.example.eattogether_neep.UI.User
import kotlinx.android.synthetic.main.activity_waiting.*
import kotlinx.android.synthetic.main.activity_waiting.img_rotate
import kotlinx.android.synthetic.main.activity_waiting_reply.*

class WaitingReplyActivity : AppCompatActivity() {
    private var roomName = ""
    private lateinit var socketReceiver: WaitingReplyActivity.WaitingReplyReceiver
    private lateinit var intentFilter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_reply)

        // loading git
        Glide.with(this).load(R.drawable.loading).into(img_rotate)

        roomName = intent.getStringExtra("roomName")!!

        socketReceiver = WaitingReplyReceiver()
        intentFilter = IntentFilter()
        with(intentFilter){
            addAction("com.example.eattogether_neep.RESULT_FINISH_PREDICT")
        }

        registerReceiver(socketReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()
        sendPing(roomName)
        //sendResult(roomName)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(socketReceiver)
    }

    private fun sendPing(roomName:String) {
        val work = Intent()
        work.putExtra("serviceFlag", "ping")
        work.putExtra("roomName", roomName)
        SocketService.enqueueWork(this, work)
    }

    private fun sendResult(roomName:String) {
        val work = Intent()
        work.putExtra("serviceFlag", "showRank")
        work.putExtra("roomName", roomName)
        SocketService.enqueueWork(this, work)
    }

    inner class WaitingReplyReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.eattogether_neep.RESULT_FINISH_PREDICT" ->{
                    val intent = Intent(this@WaitingReplyActivity, RankingActivity::class.java)
                    with(intent) {
                        intent.putExtra("roomName",roomName)
                    }
                    this@WaitingReplyActivity.startActivity(intent)
                    this@WaitingReplyActivity.finish()
                }
                else -> return
            }
        }
    }
}
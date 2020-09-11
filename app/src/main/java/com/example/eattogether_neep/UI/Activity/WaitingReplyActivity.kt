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
    private var fullNumber = -1
    private var enterNumber = 0
    private lateinit var socketReceiver: WaitingReplyActivity.WaitingReplyReceiver
    private lateinit var intentFilter: IntentFilter
    private lateinit var uuid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_reply)

        // loading git
        Glide.with(this).load(R.drawable.loading).into(img_rotate)

        roomName = intent.getStringExtra("roomName")
        uuid = User.getUUID(this)

        socketReceiver = WaitingReplyReceiver()
        intentFilter = IntentFilter()
        with(intentFilter){
            addAction("com.example.eattogether_neep.FOOD_LIST_RANK")
            addAction("com.example.eattogether_neep.ENTER_COUNT")
        }

        registerReceiver(socketReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()
        sendPreference(uuid, roomName)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(socketReceiver)
    }

    private fun sendPreference(uuid:String, roomName:String) {
        val work = Intent()
        work.putExtra("serviceFlag", "무언가")
        work.putExtra("uuid", uuid)
        work.putExtra("roomName", roomName)
        SocketService.enqueueWork(this, work)
    }

    inner class WaitingReplyReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.eattogether_neep.FOOD_LIST_RANK" -> {
                    val intent = Intent(this@WaitingReplyActivity, RankingActivity::class.java)
                    with(intent) {
                        intent.putExtra("roomName",roomName)
                    }
                    this@WaitingReplyActivity.startActivity(intent)
                    this@WaitingReplyActivity.finish()
                }
                "com.example.eattogether_neep.ENTER_COUNT" ->{
                    enterNumber = intent.getIntExtra("count",-1)
                    fullNumber = intent.getIntExtra("full", -1)
                    fullNumReply.setText(" / " + fullNumber.toString())
                    enterNumReply.setText(enterNumber.toString())
                }
                else -> return
            }
        }
    }
}
package com.example.eattogether_neep.UI.Activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.eattogether_neep.R
import com.example.eattogether_neep.socket.SocketService
import com.example.eattogether_neep.UI.User
import kotlinx.android.synthetic.main.activity_join.*

class JoinActivity : AppCompatActivity() {
    private var roomName = ""
    private lateinit var uuid: String
    private lateinit var socketReceiver: JoinReceiver
    private lateinit var intentFilter: IntentFilter
    private var resultFromServer = -1
    //val requestToServer= ApplicationController // 싱글톤 그대로 가져옴


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        uuid = User.getUUID(this)
        Log.d("Device UUID:", uuid)

        // 인원 입력 시 버튼 활성화
        edt_join_url.doOnTextChanged { text1, start, count, after ->
            if (!text1.isNullOrBlank()) {
                edt_join_url.setBackgroundResource(R.drawable.yellow_bd)
                btn_join_url.background =
                    ContextCompat.getDrawable(this, R.drawable.btn_yellow)
                btn_join_url.setTextColor(getColor(R.color.text_black))
            } else {
                edt_join_url.setBackgroundResource(R.drawable.gray_bd)
                btn_join_url.background =
                    ContextCompat.getDrawable(this, R.drawable.btn_gray)
                btn_join_url.setTextColor(getColor(R.color.text_gray2))
            }
        }

        btn_join_url.setOnClickListener {
            if (edt_join_url.text.isNullOrBlank()) {
                Toast.makeText(this, "입장 코드를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                roomName =  edt_join_url.text.toString()
                sendJoinRoom(roomName, uuid)
                //requestJoin(Integer.parseInt(edt_join_url.text.toString()))
                //localJoin(edt_join_url.text.toString())
            }
        }
        socketReceiver = JoinReceiver()
        val intentFilter = IntentFilter().apply {
            addAction("com.example.eattogether_neep.RESULT_JOIN")
        }
        registerReceiver(socketReceiver, intentFilter)

    }

    // Join By Local
    private fun localJoin(number: String) {
        val intent = Intent(this@JoinActivity, PreferenceCheckActivity::class.java)
        intent.putExtra("roomName", roomName)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(socketReceiver)
    }

    private fun sendJoinRoom(roomName: String, uuid: String) {
        val work = Intent()
        work.putExtra("serviceFlag", "createRoom")
        work.putExtra("roomName", roomName)
        work.putExtra("uuid", uuid)
        SocketService.enqueueWork(this, work)
    }

    inner class JoinReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.eattogether_neep.RESULT_JOIN" -> {
                    resultFromServer = intent.getIntExtra("result", -1)
                    if (resultFromServer == 200) {
                        localJoin(edt_join_url.text.toString())
                    } else if (resultFromServer == 400) {
                        Toast.makeText(this@JoinActivity,"입장 코드를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

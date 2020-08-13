package com.example.eattogether_neep.UI.Activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.eattogether_neep.R
import com.example.eattogether_neep.UI.User
import kotlinx.android.synthetic.main.activity_join.*
import com.example.eattogether_neep.Socket.SocketService
import retrofit2.Callback

class JoinActivity : AppCompatActivity() {
    private lateinit var uuid: String
    private lateinit var socketReceiver: JoinReciver
    private lateinit var intentFilter: IntentFilter
    private var roomID:String="961219"
    private var suc = -1
    //val requestToServer= ApplicationController // 싱글톤 그대로 가져옴

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        uuid = User.getUUID(this)
        Log.d("Device UUID:",uuid)

        buttonInteraction()

        socketReceiver=JoinReciver()
        intentFilter= IntentFilter()
        with(intentFilter){
            addAction("com.example.eattogether_neep.RESULT_ENTERENCE")
        }
        registerReceiver(socketReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()
        createRoom(roomID, uuid)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(socketReceiver)
    }


    private fun createRoom(roomID: String, deviceNumber: String) {
        val work = Intent()
        work.putExtra("roomID", roomID)
        work.putExtra("deviceNum", deviceNumber)
        SocketService.enqueueWork(this, work)
        Log.d("JoinActivity", "roomID "+ roomID+" deviceNum "+deviceNumber)
    }

    inner class JoinReciver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.eattogether_neep.RESULT_ENTERENCE" -> {
                    suc=intent.getIntExtra("suc", -1)
                    if(suc == 0) {
                        Log.d("enter","success")
                        //localJoin(edt_join_url.text.toString())
                    }
                    val leftTimeFromServer = intent.getIntExtra("leftTIme", -1)
                    Log.d( "RESULT_LEFT_TIME",leftTimeFromServer.toString())
                }
                else -> return
            }
        }
    }


    // Button interaction
    private fun buttonInteraction(){
        // 인원 입력 시 버튼 활성화
        edt_join_url.doOnTextChanged{ text1, start, count, after->
            if(!text1.isNullOrBlank()){
                btn_join_url.background =
                    ContextCompat.getDrawable(this, R.drawable.btn_yellow)
                edt_join_url.background=
                    ContextCompat.getDrawable(this, R.drawable.yellow_bd)
                btn_join_url.setTextColor(getColor(R.color.text_black))
            }else{
                btn_join_url.background =
                    ContextCompat.getDrawable(this,
                        R.drawable.btn_gray
                    )
                btn_join_url.setTextColor(getColor(R.color.text_gray2))
            }
        }

        btn_join_url.setOnClickListener{
            if (edt_join_url.text.isNullOrBlank()){
                Toast.makeText(this, "참여코드를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                //requestJoin(Integer.parseInt(edt_join_url.text.toString()))
                localJoin(edt_join_url.text.toString())

                roomID=edt_join_url.text.toString()
                createRoom(roomID, uuid)
            }
        }
    }


    // Join By Local
    private fun localJoin(number:String) {
        val intent=Intent(this@JoinActivity, PreferenceCheckActivity::class.java)
        startActivity(intent)
        finish()
    }
}

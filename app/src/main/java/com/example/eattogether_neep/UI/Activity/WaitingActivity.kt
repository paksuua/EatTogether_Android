package com.example.eattogether_neep.UI.Activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.example.eattogether_neep.R
import com.example.eattogether_neep.SOCKET.SocketService
import com.example.eattogether_neep.UI.User
import kotlinx.android.synthetic.main.activity_ranking.*
import kotlinx.android.synthetic.main.activity_waiting.*


class WaitingActivity : AppCompatActivity() {
    private var like = ""
    private var hate = ""
    private var roomName = ""
    private var fullNumber = -1
    private var enterNumber = 0
    private lateinit var socketReceiver: WaitingReciver
    private lateinit var intentFilter: IntentFilter
    private lateinit var uuid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)

        uuid = User.getUUID(this)
        // loading git
        Glide.with(this).load(R.drawable.loading).into(img_rotate)

        like = intent.getStringExtra("like")
        hate = intent.getStringExtra("hate")
        roomName = intent.getStringExtra("roomName")
        fullNumber = intent.getIntExtra("fullNum", -1)
        fullNum.setText(" / " + fullNumber.toString())
        enterNum.setText(enterNumber.toString())
        socketReceiver = WaitingReciver()
        intentFilter = IntentFilter()
        with(intentFilter){
            addAction("com.example.eattogether_neep.RESULT_OPPONENT_INFO")
            addAction("com.example.eattogether_neep.RESULT_ROOM_NAME")
        }

        registerReceiver(socketReceiver, intentFilter)

        /*cst_wait.setOnClickListener {
            val intent = Intent(this, LaunchActivity::class.java)
            startActivity(intent)
        }*/
    }

    private fun animationRotate(){
        val rotate=AnimationUtils.loadAnimation(this,
            R.anim.rotate_figure
        )
        img_rotate.animation=rotate
    }

    override fun onStart() {
        super.onStart()
        sendJoinRoom(like, hate)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(socketReceiver)
    }

    private fun sendJoinRoom(like: String, hate: String) {
        val work = Intent()
        work.putExtra("serviceFlag", "joinRoom")
        work.putExtra("uuid", uuid)
        work.putExtra("like", like)
        work.putExtra("hate", hate)
        work.putExtra("roomName", roomName)
        SocketService.enqueueWork(this, work)
    }

    inner class WaitingReciver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.eattogether_neep.RESULT_OPPONENT_INFO" -> {
                    roomName = intent.getStringExtra("roomName")!!
                    enterNumber++

                    val intent = Intent(this@WaitingActivity, EmotionAnalysisActivity::class.java)
                    with(intent) {
                        putExtra("roomName", roomName)
                    }

                    if(enterNumber == fullNumber){
                        this@WaitingActivity.startActivity(intent)
                        this@WaitingActivity.finish()
                    }
                }
                "com.example.eattogether_neep.RESULT_ROOM_NUM" -> roomName =
                    intent.getStringExtra("roomName")!!

                else -> return
            }


        }
    }
}



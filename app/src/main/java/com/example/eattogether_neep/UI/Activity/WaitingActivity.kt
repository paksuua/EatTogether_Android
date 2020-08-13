package com.example.eattogether_neep.UI.Activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.example.eattogether_neep.R
import com.example.eattogether_neep.UI.User
import kotlinx.android.synthetic.main.activity_preference_check.*
import kotlinx.android.synthetic.main.activity_ranking.*
import kotlinx.android.synthetic.main.activity_waiting.*


class WaitingActivity : AppCompatActivity() {
    //private lateinit var socketReciever: MatchProcReciver
    private lateinit var intentFilter: IntentFilter
    private val SOCKET_URL="http://10.0.2.2:3001"
    private var mSocket: io.socket.client.Socket? = null
    var second: Int=0

    var uuid: String=""
    var roomNumber: String=""
    var favorite: String=""
    var hate: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)

        InitUI()
        //socketReceiver = MatchProcReciver()
        intentFilter = IntentFilter()
        with(intentFilter){
            addAction("com.team.runnershi.RESULT_LEFT_TIME")
            addAction("com.team.runnershi.RESULT_OPPONENT_INFO")
            addAction("com.team.runnershi.RESULT_ROOM_NAME")
        }
        //registerReceiver(socketReceiver, intentFilter)

        // loading git
        Glide.with(this).load(R.drawable.loading).into(img_rotate)


        cst_wait.setOnClickListener {
            val intent = Intent(this, LaunchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun InitUI(){
        uuid = intent.getStringExtra("uuid")
        roomNumber = intent.getStringExtra("roomNumber")
        favorite = intent.getStringExtra("favorite")
        hate = intent.getStringExtra("hate")


    }

    private fun animationRotate(){
        val rotate=AnimationUtils.loadAnimation(this,
            R.anim.rotate_figure
        )
        img_rotate.animation=rotate
    }
}



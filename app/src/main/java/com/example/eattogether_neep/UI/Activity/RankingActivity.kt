package com.example.eattogether_neep.UI.Activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eattogether_neep.Data.RankingItem
import com.example.eattogether_neep.Network.Get.GetRankingResponse
import com.example.eattogether_neep.Network.Network.ApplicationController
import com.example.eattogether_neep.Network.NetworkService
import com.example.eattogether_neep.R
import com.example.eattogether_neep.SOCKET.SocketService
import com.example.eattogether_neep.UI.Adapter.RankingItemRVAdapter
import kotlinx.android.synthetic.main.activity_ranking.*
import kotlinx.android.synthetic.main.activity_waiting.*
import retrofit2.Call
import retrofit2.Response

class RankingActivity : AppCompatActivity() {

    private var roomName = ""
    private lateinit var socketReceiver: RankingReceiver
    private lateinit var intentFilter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        // X 버튼 클릭시 메인페이지로
        btn_close_ranking.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //roomName = intent.getStringExtra("roomName")
        roomName = "835279"
        socketReceiver = RankingReceiver()
        intentFilter = IntentFilter()
        with(intentFilter){
            addAction("com.example.eattogether_neep.FOOD_LIST_RANK")
        }

        registerReceiver(socketReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()
        sendRoomNAme(roomName)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(socketReceiver)
    }

    private fun sendRoomNAme(roomName:String) {
        val work = Intent()
        work.putExtra("serviceFlag", "roomName")
        work.putExtra("roomName", roomName)
        SocketService.enqueueWork(this, work)
    }

    inner class RankingReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.eattogether_neep.FOOD_LIST_RANK" -> {
                    val f_name = intent.getStringExtra("food_name")!!
                    val f_img = intent.getStringExtra("food_img")!!

                    Glide.with(this@RankingActivity).load(f_name[0]).into(rv_ranking_img1)
                    rv_ranking_food_name1.text = f_img[0].toString()
                    Glide.with(this@RankingActivity).load(f_name[1]).into(rv_ranking_img2)
                    rv_ranking_food_name2.text = f_img[1].toString()
                    Glide.with(this@RankingActivity).load(f_name[2]).into(rv_ranking_img3)
                    rv_ranking_food_name3.text = f_img[2].toString()
                    Glide.with(this@RankingActivity).load(f_name[3]).into(rv_ranking_img4)
                    rv_ranking_food_name4.text = f_img[3].toString()
                    Glide.with(this@RankingActivity).load(f_name[4]).into(rv_ranking_img5)
                    rv_ranking_food_name5.text = f_img[4].toString()
                    Glide.with(this@RankingActivity).load(f_name[5]).into(rv_ranking_img6)
                    rv_ranking_food_name6.text = f_img[5].toString()
                    Glide.with(this@RankingActivity).load(f_name[6]).into(rv_ranking_img7)
                    rv_ranking_food_name7.text = f_img[6].toString()
                    Glide.with(this@RankingActivity).load(f_name[7]).into(rv_ranking_img8)
                    rv_ranking_food_name8.text = f_img[7].toString()
                    Glide.with(this@RankingActivity).load(f_name[8]).into(rv_ranking_img9)
                    rv_ranking_food_name9.text = f_img[8].toString()
                    Glide.with(this@RankingActivity).load(f_name[9]).into(rv_ranking_img10)
                    rv_ranking_food_name10.text = f_img[9].toString()
                }
                else -> return
            }
        }
    }
}
package com.example.eattogether_neep.UI.Activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.eattogether_neep.R
import com.example.eattogether_neep.SOCKET.SocketService
import kotlinx.android.synthetic.main.activity_ranking.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.pm.PackageManager

class RankingActivity : AppCompatActivity() {

    private var roomName = ""
    private lateinit var socketReceiver: RankingReceiver
    private lateinit var intentFilter: IntentFilter

    private var mCurrentLng = 0.0
    private var mCurrentLat = 0.0
    private val LOCATION_PERMISSION_REQ_CODE = 1000;
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        // X 버튼 클릭시 메인페이지로
        btn_close_ranking.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        roomName = intent.getStringExtra("roomName")!!
        Toast.makeText(this, "Room Number"+ roomName, Toast.LENGTH_LONG).show()
        //roomName = "835197"
        socketReceiver = RankingReceiver()
        intentFilter = IntentFilter()
        with(intentFilter){
            addAction("com.example.eattogether_neep.FOOD_LIST_RANK")
        }

        registerReceiver(socketReceiver, intentFilter)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        num1.setOnClickListener {
            showMap(Uri.parse("kakaomap://search?q=" + rv_ranking_food_name1.text + "&p=" + mCurrentLat + "," + mCurrentLng))
        }
        num2.setOnClickListener {
            showMap(Uri.parse("kakaomap://search?q=" + rv_ranking_food_name2.text + "&p=" + mCurrentLat + "," + mCurrentLng))
        }
        num3.setOnClickListener {
            showMap(Uri.parse("kakaomap://search?q=" + rv_ranking_food_name3.text + "&p=" + mCurrentLat + "," + mCurrentLng))
        }
        num4.setOnClickListener {
            showMap(Uri.parse("kakaomap://search?q=" + rv_ranking_food_name4.text + "&p=" + mCurrentLat + "," + mCurrentLng))
        }
        num5.setOnClickListener {
            showMap(Uri.parse("kakaomap://search?q=" + rv_ranking_food_name5.text + "&p=" + mCurrentLat + "," + mCurrentLng))
        }
        num6.setOnClickListener {
            showMap(Uri.parse("kakaomap://search?q=" + rv_ranking_food_name6.text + "&p=" + mCurrentLat + "," + mCurrentLng))
        }
        num7.setOnClickListener {
            showMap(Uri.parse("kakaomap://search?q=" + rv_ranking_food_name7.text + "&p=" + mCurrentLat + "," + mCurrentLng))
        }
        num8.setOnClickListener {
            showMap(Uri.parse("kakaomap://search?q=" + rv_ranking_food_name8.text + "&p=" + mCurrentLat + "," + mCurrentLng))
        }
        num9.setOnClickListener {
            showMap(Uri.parse("kakaomap://search?q=" + rv_ranking_food_name9.text + "&p=" + mCurrentLat + "," + mCurrentLng))
        }
        num10.setOnClickListener {
            showMap(Uri.parse("kakaomap://search?q=" + rv_ranking_food_name10.text + "&p=" + mCurrentLat + "," + mCurrentLng))
        }
    }

    override fun onStart() {
        super.onStart()
        sendRoomName(roomName)
        getCurrentLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(socketReceiver)
    }

    private fun getCurrentLocation() {
        // checking location permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE);
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // getting the last known or current location
                mCurrentLat = location.latitude
                mCurrentLng = location.longitude
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed on getting current location",
                    Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQ_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                } else {
                    // permission denied
                    Toast.makeText(this, "You need to grant permission to access location",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendRoomName(roomName:String) {
        val work = Intent()
        work.putExtra("serviceFlag", "showRank")
        work.putExtra("roomName", roomName)
        SocketService.enqueueWork(this, work)
    }

    inner class RankingReceiver() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.eattogether_neep.FOOD_LIST_RANK" -> {
                    val f_name = intent.getStringArrayExtra("food_name")!!
                    val f_img = intent.getStringArrayExtra("food_img")!!

                    Glide.with(this@RankingActivity).load(f_img[0]).into(rv_ranking_img1)
                    rv_ranking_food_name1.text = f_name[0].toString()
                    Glide.with(this@RankingActivity).load(f_img[1]).into(rv_ranking_img2)
                    rv_ranking_food_name2.text = f_name[1].toString()
                    Glide.with(this@RankingActivity).load(f_img[2]).into(rv_ranking_img3)
                    rv_ranking_food_name3.text = f_name[2].toString()
                    Glide.with(this@RankingActivity).load(f_img[3]).into(rv_ranking_img4)
                    rv_ranking_food_name4.text = f_name[3].toString()
                    Glide.with(this@RankingActivity).load(f_img[4]).into(rv_ranking_img5)
                    rv_ranking_food_name5.text = f_name[4].toString()
                    Glide.with(this@RankingActivity).load(f_img[5]).into(rv_ranking_img6)
                    rv_ranking_food_name6.text = f_name[5].toString()
                    Glide.with(this@RankingActivity).load(f_img[6]).into(rv_ranking_img7)
                    rv_ranking_food_name7.text = f_name[6].toString()
                    Glide.with(this@RankingActivity).load(f_img[7]).into(rv_ranking_img8)
                    rv_ranking_food_name8.text = f_name[7].toString()
                    Glide.with(this@RankingActivity).load(f_img[8]).into(rv_ranking_img9)
                    rv_ranking_food_name9.text = f_name[8].toString()
                    Glide.with(this@RankingActivity).load(f_img[9]).into(rv_ranking_img10)
                    rv_ranking_food_name10.text = f_name[9].toString()
                }
                else -> return
            }
        }
    }

    fun showMap(geoLocation: Uri?) {
        var intent: Intent
        try {
            intent = Intent(Intent.ACTION_VIEW, geoLocation)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "장소찾기에는 카카오맵이 필요합니다. 다운받아주시길 바랍니다.", Toast.LENGTH_SHORT).show()
            intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=net.daum.android.map&hl=ko"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}
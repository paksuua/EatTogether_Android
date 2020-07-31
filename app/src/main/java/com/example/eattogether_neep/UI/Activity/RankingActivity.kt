package com.example.eattogether_neep.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eattogether_neep.Data.RankingItem
import com.example.eattogether_neep.Network.Get.GetRankingResponse
import com.example.eattogether_neep.Network.Network.ApplicationController
import com.example.eattogether_neep.Network.NetworkService
import com.example.eattogether_neep.R
import com.example.eattogether_neep.UI.Adapter.RankingItemRVAdapter
import kotlinx.android.synthetic.main.activity_ranking.*
import retrofit2.Call
import retrofit2.Response

class RankingActivity : AppCompatActivity() {

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    lateinit var rankingItemRVAdaptter: RankingItemRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        // X 버튼 클릭시 메인페이지로
        btn_close_ranking.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        var dataList:ArrayList<RankingItem> = ArrayList()
        dataList.add(RankingItem(
            1,
            "https://www.lark.com/wp-content/uploads/2019/11/jason-briscoe-7MAjXGUmaPw-unsplash.jpg",
            1,
             "마라샹궈"))
        dataList.add(RankingItem(
            2,
            "https://www.lark.com/wp-content/uploads/2019/11/jason-briscoe-7MAjXGUmaPw-unsplash.jpg",
            2,
            "마라샹궈"))
        dataList.add(RankingItem(
            3,
            "https://www.lark.com/wp-content/uploads/2019/11/jason-briscoe-7MAjXGUmaPw-unsplash.jpg",
            3,
            "마라샹궈"))
        dataList.add(RankingItem(
            4,
            "https://www.lark.com/wp-content/uploads/2019/11/jason-briscoe-7MAjXGUmaPw-unsplash.jpg",
            4,
            "마라샹궈"))
        dataList.add(RankingItem(
            5,
            "https://www.lark.com/wp-content/uploads/2019/11/jason-briscoe-7MAjXGUmaPw-unsplash.jpg",
            5,
            "마라샹궈"))
        dataList.add(RankingItem(
            6,
            "https://www.lark.com/wp-content/uploads/2019/11/jason-briscoe-7MAjXGUmaPw-unsplash.jpg",
            6,
            "마라샹궈"))
        dataList.add(RankingItem(
            7,
            "https://www.lark.com/wp-content/uploads/2019/11/jason-briscoe-7MAjXGUmaPw-unsplash.jpg",
            7,
            "마라샹궈"))

        rankingItemRVAdaptter = RankingItemRVAdapter(this, dataList)
        rv_ranking.adapter = rankingItemRVAdaptter
        rv_ranking.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        //getRankingResponse()
    }

    private fun getRankingResponse(){
        //val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWR4IjoxLCJuaWNrbmFtZSI6IuyEne2ZqSIsImlhdCI6MTU2ODIxNzMyNCwiZXhwIjoxNTc5MDE3MzI0LCJpc3MiOiJiYWJ5Q2xvc2V0In0.pGluiC04m2sXWdtHwWKR8SdSMQYS_kSd_uumifKBz18"
        //val token = SharedPreference.getUserToken(ctx)

        val postIdx = 1

        val getRankingResponse = networkService.getRankingResponse("application/json", postIdx)
        getRankingResponse.enqueue(object : retrofit2.Callback<GetRankingResponse>{
            override fun onFailure(call: Call<GetRankingResponse>, t: Throwable) {
                t.printStackTrace()
            }
            override fun onResponse(call: Call<GetRankingResponse>, response: Response<GetRankingResponse>) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        val tmp: ArrayList<RankingItem> = response.body()!!.data!!
                        rankingItemRVAdaptter.dataList = tmp
                        rankingItemRVAdaptter.notifyDataSetChanged()
                    }
                    else if (response.body()!!.status == 400){
                        Log.e("tag", "No token")
                    }
                }
            }
        })
    }

}

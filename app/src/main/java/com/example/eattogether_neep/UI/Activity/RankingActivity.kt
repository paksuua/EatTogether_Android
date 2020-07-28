package com.example.eattogether_neep.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eattogether_neep.Data.RankingItem
import com.example.eattogether_neep.Network.Get.GetRankingResponse
import com.example.eattogether_neep.Network.Get.Getrankdata
import com.example.eattogether_neep.Network.Network.ApplicationController
import com.example.eattogether_neep.Network.NetworkService
import com.example.eattogether_neep.R
import com.example.eattogether_neep.UI.Adapter.RankingItemRVAdapter
import kotlinx.android.synthetic.main.activity_ranking.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RankingActivity : AppCompatActivity() {

    val networkService: NetworkService by lazy {
        ApplicationController.instance.networkService
    }

    lateinit var rankingItemRVAdaptter: RankingItemRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

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
    }
}

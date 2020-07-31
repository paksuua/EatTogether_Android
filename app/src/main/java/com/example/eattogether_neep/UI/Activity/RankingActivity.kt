package com.example.eattogether_neep.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eattogether_neep.Network.Get.GetRankingResponse
import com.example.eattogether_neep.Network.Get.Getrankdata
import com.example.eattogether_neep.Network.Network.ApplicationController
import com.example.eattogether_neep.Network.NetworkService
import com.example.eattogether_neep.R
import kotlinx.android.synthetic.main.activity_ranking.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RankingActivity : AppCompatActivity() {

    val networkService: NetworkService by lazy {
        ApplicationController.networkService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        ll_ranking.setOnClickListener {
            val intent=Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // ranking 리스트 가져오기
        //getRankingResponse()
    }

    private fun getRankingResponse() {

        val getRankingResponse = networkService.getRankingResponse("application/json")
        getRankingResponse.enqueue(object : Callback<GetRankingResponse> {
            override fun onFailure(call: Call<GetRankingResponse>, t: Throwable) {
                //toast("error")
            }
            override fun onResponse(call: Call<GetRankingResponse>, response: Response<GetRankingResponse>) {
                if (response.isSuccessful) {
                    if (response.body()!!.status == 200) {
                        var tmp: Getrankdata = response.body()!!.data!!
                        txt_r1_name.text = tmp.rank1
                        txt_r2_name.text = tmp.rank2
                        txt_r3_name.text = tmp.rank3
                        txt_r4_name.text = tmp.rank4
                        txt_r5_name.text = tmp.rank5
                        txt_r6_name.text = tmp.rank6
                        txt_r7_name.text = tmp.rank7
                        txt_r8_name.text = tmp.rank8
                        txt_r9_name.text = tmp.rank9
                        txt_r10_name.text = tmp.rank10
                    }
                }
            }
        })

    }

}

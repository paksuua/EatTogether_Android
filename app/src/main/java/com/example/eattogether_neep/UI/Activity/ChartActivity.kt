package com.example.eattogether_neep.UI.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eattogether_neep.network.Get.GetChartRequest
import com.example.eattogether_neep.network.Get.GetChartResponse
import com.example.eattogether_neep.network.Get.chart1
import com.example.eattogether_neep.network.Get.totalD
import com.example.eattogether_neep.network.Network.ApplicationController
import com.example.eattogether_neep.R
import com.example.eattogether_neep.UI.Adapter.ChartOverviewRecyclerViewAdapter
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.activity_chart.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChartActivity : AppCompatActivity() {
    private var roomName = ""
    lateinit var chartOverviewRecyclerViewAdapter: ChartOverviewRecyclerViewAdapter
    val requestToServer = ApplicationController
    var entries = ArrayList<BarEntry>()
    var food_list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        btn_chart_back.setOnClickListener {
            val intent = Intent(this, RankingActivity::class.java)
            startActivity(intent)
        }
    }
    inner class MyXAxisFormatter : ValueFormatter(){
        private val days = food_list
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }

    private fun configureRecyclerView() {
        var RVdataList: ArrayList<chart1> = ArrayList()

        chartOverviewRecyclerViewAdapter = ChartOverviewRecyclerViewAdapter(this, RVdataList)
        rv_chart_overview.adapter = chartOverviewRecyclerViewAdapter
        rv_chart_overview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        getChartResponse()
    }

    override fun onStart() {
        super.onStart()
        roomName = intent.getStringExtra("roomName")!!
        configureRecyclerView()
    }

    private fun getChartResponse(){
        requestToServer.networkService.getChartResponse(
            GetChartRequest(roomName)).enqueue(object : Callback<GetChartResponse> {
            override fun onFailure(call: Call<GetChartResponse>, t: Throwable) {
                t.printStackTrace()
            }
            override fun onResponse(call: Call<GetChartResponse>, response: Response<GetChartResponse>) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        var tmp1: totalD = response.body()!!.data.rankChart!!
                        var x = 1.2f
                        for(i in 0..tmp1.foodName.size-1){
                            food_list.add(tmp1.foodName[i])
                            entries.add(BarEntry(x++.toFloat(),tmp1.totalPred[i].toFloat()))
                        }
                        var set = BarDataSet(entries,"DataSet")//데이터셋 초기화 하기
                        //set.color = ContextCompat.getColor(this@ChartActivity, R.color.main_yellow) // total 그래프 색

                        val dataSet :ArrayList<IBarDataSet> = ArrayList()
                        dataSet.add(set)
                        val data = BarData(dataSet)
                        data.barWidth = 0.8f//막대 너비 설정하기

                        final_chart.run {
                            description.isEnabled = false //차트 옆에 별도로 표기되는 description이다. false로 설정하여 안보이게 했다.
                            setMaxVisibleValueCount(100)
                            setPinchZoom(true) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
                            setDrawBarShadow(false)//그래프의 그림자
                            setDrawGridBackground(false)//격자구조 넣을건지
                            xAxis.run {
                                position = XAxis.XAxisPosition.BOTTOM//X축을 아래에다가 둔다.
                                granularity = 0.8f // 1 단위만큼 간격 두기
                                setDrawAxisLine(true) // 축 그림
                                setDrawGridLines(false) // 격자
                                valueFormatter = MyXAxisFormatter() // 축 라벨 값 바꿔주기 위함
                                textSize = 10f // 텍스트 크기
                            }
                            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
                            setTouchEnabled(true) // 그래프 터치해도 아무 변화없게 막음
                            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
                            legend.isEnabled = false //차트 범례 설정

                            this.data = data //차트의 데이터를 data로 설정해줌.
                            setFitBars(true)
                            invalidate()
                        }
                        var tmp: ArrayList<chart1> = response.body()!!.data.foods!!
                        chartOverviewRecyclerViewAdapter.dataList!!.addAll(tmp)
                        chartOverviewRecyclerViewAdapter.notifyDataSetChanged()
                    }
                    else if (response.body()!!.status == 400){
                        Log.e("tag", "server error")
                    }
                }
            }
        })
    }

}
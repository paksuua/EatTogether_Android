package com.example.eattogether_neep.UI.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.eattogether_neep.Network.Get.chart1
import com.example.eattogether_neep.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet

class ChartOverviewRecyclerViewAdapter (val ctx: Context, var dataList: ArrayList<chart1>): RecyclerView.Adapter<ChartOverviewRecyclerViewAdapter.Holder>() {
    var foodname:String = ""
    var entries = ArrayList<BarEntry>()
    var entries2 = ArrayList<BarEntry>()
    var happy = ArrayList<Float>()
    var bad = ArrayList<Float>()
    var u_num = ArrayList<String>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(ctx)
            .inflate(com.example.eattogether_neep.R.layout.rv_chart, viewGroup, false)
        return Holder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: Holder, position1: Int) {
        holder.name.text = dataList[position1].food_name
        foodname = dataList[position1].food_name

        happy = dataList[position1].happy
        bad = dataList[position1].bad

        val cnt = bad.size
        var x=0.2f
        var x2=0.5f

        for(i in 0..cnt){
            entries.add(BarEntry(x++, happy[i]))
            entries2.add(BarEntry(x2++, bad[i]))
            u_num.add("user"+i+1)
        }

        u_num.add("total")

        var set = BarDataSet(entries,"DataSet")//데이터셋 초기화 하기
        var set2 = BarDataSet(entries2,"DataSet")//데이터셋 초기화 하기
        set2.color = ContextCompat.getColor(ctx, R.color.main_yellow)

        val dataSet :ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)
        dataSet.add(set2)
        val data = BarData(dataSet)
        data.barWidth = 0.8f//막대 너비 설정하기

        holder.chart.run {
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
    }
    inner class MyXAxisFormatter : ValueFormatter(){
        private val days = u_num
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById(com.example.eattogether_neep.R.id.rv_chart_form_name) as TextView
        var chart = itemView.findViewById(com.example.eattogether_neep.R.id.rv_chart) as BarChart
    }
}
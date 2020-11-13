package com.example.eattogether_neep.UI.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.eattogether_neep.network.Get.chart1
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
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): Holder {
        val view: View = LayoutInflater.from(ctx)
            .inflate(com.example.eattogether_neep.R.layout.rv_chart, viewGroup, false)
        return Holder(view)
    }
    var u_num = ArrayList<String>()

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: Holder, position1: Int) {
        var entries = ArrayList<BarEntry>()
        var entries2 = ArrayList<BarEntry>()
        holder.name.text = dataList[position1].foodName
        var foodname:String = dataList[position1].foodName
        val happy: ArrayList<Float> = dataList[position1].happy
        val bad: ArrayList<Float> = dataList[position1].neutral

        var x = 0.2f
        var x2 = 0.5f

        for (i in 0..bad.size - 1) {
            entries.add(BarEntry(x++.toFloat(), happy[i].toFloat()))
            entries2.add(BarEntry(x2++.toFloat(), bad[i].toFloat()))
            u_num.add("user" + i)
        }

        u_num[bad.size-1] = "total"

        var set = BarDataSet(entries, "DataSet")
        set.color = ContextCompat.getColor(ctx, R.color.happy) // happy 그래프 색

        var set2 = BarDataSet(entries2, "DataSet")
        set2.color = ContextCompat.getColor(ctx, R.color.text_gray) // bad 그래프 색

        var dataSet: ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)
        dataSet.add(set2)
        var data = BarData(dataSet)
        data.barWidth = 0.5f//막대 너비

        holder.chart.run {
            description.isEnabled = false //차트 옆에 별도로 표기되는 description이다. false로 설정하여 안보이게 했다.
            setMaxVisibleValueCount(100)
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false)//그래프의 그림자
            setDrawGridBackground(false)//격자구조 넣을건지
            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMaximum = 101f //100 위치에 선을 그리기 위해 101f로 맥시멈을 정해주었다
                axisMinimum = 0f // 최소값 0
                granularity = 20f // 50 단위마다 선을 그리려고 granularity 설정 해 주었다.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM//X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                valueFormatter = MyXAxisFormatter() // 축 라벨 값 바꿔주기 위함
                textSize = 10f // 텍스트 크기
            }
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
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
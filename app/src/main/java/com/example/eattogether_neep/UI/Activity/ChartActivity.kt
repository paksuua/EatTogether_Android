package com.example.eattogether_neep.UI.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.eattogether_neep.R
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.activity_chart.*

class ChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        btn_chart_back.setOnClickListener {   finish()   }

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1.2f,20.0f))
        entries.add(BarEntry(2.2f,70.0f))
        entries.add(BarEntry(3.2f,30.0f))
        entries.add(BarEntry(4.2f,90.0f))
        entries.add(BarEntry(5.2f,70.0f))
        entries.add(BarEntry(6.2f,30.0f))
        entries.add(BarEntry(7.2f,90.0f))
        entries.add(BarEntry(8.2f,20.0f))
        entries.add(BarEntry(9.2f,20.0f))
        entries.add(BarEntry(10.2f,20.0f))

        val entries2 = ArrayList<BarEntry>()
        entries2.add(BarEntry(1.5f,10.0f))
        entries2.add(BarEntry(2.5f,30.0f))
        entries2.add(BarEntry(3.5f,50.0f))
        entries2.add(BarEntry(4.5f,90.0f))
        entries2.add(BarEntry(5.5f,90.0f))
        entries2.add(BarEntry(6.5f,80.0f))
        entries2.add(BarEntry(7.5f,20.0f))
        entries2.add(BarEntry(7.5f,20.0f))
        entries2.add(BarEntry(7.5f,20.0f))

        var set = BarDataSet(entries,"DataSet")//데이터셋 초기화 하기
        //set.color = ContextCompat.getColor(this,)
        var set2 = BarDataSet(entries2,"DataSet")//데이터셋 초기화 하기
        set2.color = ContextCompat.getColor(this,R.color.main_yellow)

        val dataSet :ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)
        //dataSet.add(set2)
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
    }
    inner class MyXAxisFormatter : ValueFormatter(){
        private val days = arrayOf("1차","2차","3차","4차","5차","6차","7차","8차","9차","10차") // 음식이름 나열
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }
}
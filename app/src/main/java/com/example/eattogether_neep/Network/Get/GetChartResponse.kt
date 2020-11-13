package com.example.eattogether_neep.Network.Get

data class GetChartResponse(
val status: Int,
val success: Boolean,
val message: String,
val data: Chartdata
)

data class Chartdata(
    val rankChart: totalD,
    val foods: ArrayList<chart1>
)

data class totalD(
    val foodName:ArrayList<String>,
    val totalPred:ArrayList<Float>
)

data class chart1(
    val foodName: String,
    val happy: ArrayList<Float>,
    val neutral: ArrayList<Float>
)
package com.example.eattogether_neep.Network.Get

data class GetChartResponse(
val status: Int,
val success: Boolean,
val message: String,
val data: Chartdata
)

data class Chartdata(
    val total: totalD,
    val cc: ArrayList<chart1>
)

data class totalD(
    val stat:ArrayList<Float>,
    val food_list:ArrayList<String>
)

data class chart1(
    val food_name: String,
    val happy: ArrayList<Float>,
    val bad: ArrayList<Float>
)
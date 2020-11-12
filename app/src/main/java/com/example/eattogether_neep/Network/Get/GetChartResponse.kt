package com.example.eattogether_neep.Network.Get

data class GetChartResponse(
val status: Int,
val success: Boolean,
val message: String,
val data: Chartdata
)

data class Chartdata(
    val food_name: String,
    val happy: ArrayList<Double>,
    val bad: ArrayList<Double>,
)
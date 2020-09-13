package com.example.eattogether_neep.Network.Post

data class PostEmotionResponse (
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: AnalysisData?
)

data class AnalysisData(
    var menuOrder:Int,
    var happiness:Float
)
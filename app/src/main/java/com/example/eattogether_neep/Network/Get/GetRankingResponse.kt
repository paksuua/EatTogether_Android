package com.example.eattogether_neep.Network.Get

data class GetRankingResponse (
    val status: Int,
    val success:Boolean,
    val message: String,
    val data: Getrankdata
)
data class Getrankdata(
    var rank1:String,
    var rank2:String,
    var rank3:String,
    var rank4:String,
    var rank5:String,
    var rank6:String,
    var rank7:String,
    var rank8:String,
    var rank9:String,
    var rank10:String
)
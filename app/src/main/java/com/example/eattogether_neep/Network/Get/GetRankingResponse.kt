package com.example.eattogether_neep.Network.Get

import com.example.eattogether_neep.Data.RankingItem

data class GetRankingResponse (
    val status: Int,
    val success:Boolean,
    val message: String,
    val data: ArrayList<RankingItem>
)
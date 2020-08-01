package com.example.eattogether_neep.Network.Post

/**
 * success = 성공여부
 * message = ex: "감정데이터 응답 성공"
 * data = 감정 분석 결과
 *
 * preference = 긍정 or 부정
 * prefer_data = 0.9385
 */

data class PostJoinRequest (
    val uuid: String,
    val join_code:String
)
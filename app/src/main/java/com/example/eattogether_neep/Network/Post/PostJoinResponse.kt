package com.example.eattogether_neep.Network.Post

/**
 * status = 상태 코드
 * success = 성공여부
 * message = ex: "참여 응답 성공"
 */

data class PostJoinResponse (
    val state: Int,
    val success: String,
    val message: String
)
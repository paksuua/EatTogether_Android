package com.example.eattogether_neep.network.Post

/**
 * status = 상태 코드
 * success = 성공여부
 * message = ex: "입장 코드 생성 응답 성공"
 * join_code = 입장 코드
 */

data class PostMakeUrlResponse (
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: Data?
)

data class Data (
    val roomID: Int
)
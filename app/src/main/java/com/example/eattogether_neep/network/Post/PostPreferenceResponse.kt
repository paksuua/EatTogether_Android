package com.example.eattogether_neep.network.Post

/**
 * status = 상태 코드
 * success = 성공여부
 * message = ex: "감정데이터 응답 성공"
 * data = 감정 분석 결과
 *
 * preference = 긍정 or 부정
 * prefer_data = 0.9385
 */

data class PostPreferenceResponse (
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: Preference
)

data class Preference(
    val preference: String,
    val prefer_data: String
)
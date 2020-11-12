package com.example.eattogether_neep.Network

import com.example.eattogether_neep.Network.Post.*
import com.example.eattogether_neep.Network.Get.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface NetworkService {
    //방 생성
    @POST("/room/checkroomid")
    fun postMakeUrlRequest(
        @Body body : PostMakeUrlRequest
    ) : Call<PostMakeUrlResponse>

    @Multipart
    @POST("/room/list")
    fun transferImage(
        @Part img: MultipartBody.Part,
        @Part("roomID") roomName: RequestBody,
        @Part("deviceNum") uuid: RequestBody,
        @Part("imgOrder") imageOrder: RequestBody
    ): Call<PostEmotionResponse>

    //참여 코드 입력
    @POST("/user/join")
    fun postJoinRequest(
        @Body body : PostJoinRequest
    ) : Call<PostJoinResponse>

    //차트 데이터 받아오기
    @GET("/user/chart")
    fun getChartResponse(
        @Header("Content-Type") content_type: String,
        @Body() body: JsonObject
    ): Call<GetChartResponse>
}

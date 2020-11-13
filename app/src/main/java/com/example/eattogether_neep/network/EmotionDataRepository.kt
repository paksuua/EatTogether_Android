package com.example.eattogether_neep.network

import com.example.eattogether_neep.network.Network.ApplicationController
import com.example.eattogether_neep.network.Post.PostEmotionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

class EmotionDataRepository {
    fun transferImage(
        image:  MultipartBody.Part,
        roomName: RequestBody,
        uuid: RequestBody,
        imageOrder: RequestBody
    ): Call<PostEmotionResponse> {
/*        return ApplicationController.networkService.transferImage(image, uuid, imageOrder)*/
        return ApplicationController.networkService.transferImage(image, roomName, uuid, imageOrder)
    }
}
package com.example.eattogether_neep.Network

import com.example.eattogether_neep.Network.Network.ApplicationController
import com.example.eattogether_neep.Network.Post.PostEmotionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

class EmotionDataRepository {
    fun transferImage(
        image:  MultipartBody.Part,
        uuid: RequestBody,
        imageOrder: RequestBody
    ): Call<PostEmotionResponse> {
        return ApplicationController.networkService.transferImage(image, uuid, imageOrder)
    }
}
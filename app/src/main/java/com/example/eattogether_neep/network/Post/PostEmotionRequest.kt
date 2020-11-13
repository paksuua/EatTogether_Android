package com.example.eattogether_neep.network.Post

import okhttp3.MultipartBody

data class PostEmotionRequest (
    val image: MultipartBody.Part,
    val uuid: String,
    val imageOrder: String
)
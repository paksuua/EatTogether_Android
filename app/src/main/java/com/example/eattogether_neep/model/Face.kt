package com.example.eattogether_neep.model

import android.graphics.Bitmap
import android.graphics.Rect

data class Face(
    val emotion: Emotion,
    val bitmap: Bitmap,
    val imageRect: Rect,
    val faceRect: Rect
)

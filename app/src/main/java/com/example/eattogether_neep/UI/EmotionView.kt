package com.example.eattogether_neep.UI

/*
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.eattogether_neep.model.Emotion
import com.example.eattogether_neep.model.Face

class EmotionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var faces: List<Face> = emptyList()
        set(value) {
            if (field != value) {
                field = value
                postInvalidate()
            }
        }

    private val facePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLUE
        strokeWidth = 10f
    }

    private val textPaint = Paint().apply {
        textSize = 60f
        color = Color.BLUE
    }

    private var previewRect = Rect()

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        previewRect = Rect(right - PREVIEW_SIZE, bottom - PREVIEW_SIZE, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (face in faces) {
            val faceRect = face.scaledFaceRect()
            canvas.drawRect(faceRect, facePaint)
            canvas.drawEmotion(faceRect, face.emotion)
        }

        if (faces.isNotEmpty()) {
            val firstFace = faces.first()
            canvas.drawBitmap(firstFace.bitmap, null, previewRect, facePaint)
        }
    }

    private fun Face.scaledFaceRect(): RectF {
        val scaleY = height / imageRect.height().toFloat()
        val scaleX = width / imageRect.width().toFloat()

        return RectF(
            width - faceRect.left * scaleX,
            faceRect.top * scaleY,
            width - faceRect.right * scaleX,
            faceRect.bottom * scaleY
        )
    }

    private fun Canvas.drawEmotion(faceRect: RectF, emotion: Emotion) {
        drawText(
            emotion.text,
            faceRect.right,
            faceRect.top - textPaint.textSize / 2,
            textPaint
        )
    }

    companion object {
        private const val PREVIEW_SIZE = 256
    }
}
*/

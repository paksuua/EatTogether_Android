package com.example.eattogether_neep.model

@Suppress("unused")
enum class Emotion(
    val id: Int,
    val text: String
) {
    NOT_FOUND(-1, "Not found"),
    ANGRY(0, "Angry"),
    DISGUST(1, "Disgust"),
    FEAR(2, "Fear"),
    HAPPY(3, "Happy"),
    SAD(4, "Sad"),
    SURPRISE(5, "Surprise"),
    NEUTRAL(6, "Neutral");

    companion object {
        fun fromResult(result: Array<FloatArray>): Emotion {
            val index = result[0].indexOf(result[0].max() ?: 0f)
            return values().find { it.id == index } ?: NOT_FOUND
        }
    }
}

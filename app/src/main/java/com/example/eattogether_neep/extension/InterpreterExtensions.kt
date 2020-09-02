package com.example.eattogether_neep.extension

import org.tensorflow.lite.Interpreter

fun Interpreter.run(input: Any): Array<FloatArray> {
    val output = arrayOf(FloatArray(7))
    run(input, output)
    return output
}

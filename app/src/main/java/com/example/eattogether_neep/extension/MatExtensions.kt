package com.example.eattogether_neep.extension

import android.graphics.Bitmap
import android.graphics.Rect
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

fun createMat(bitmap: Bitmap): Mat {
    val mat = Mat()
    Utils.bitmapToMat(bitmap, mat)
    return mat
}

fun Mat.crop(rect: Rect): Mat {
    return Mat(this, rect.toOpenCVRect())
}

fun Mat.grayScale(): Mat {
    val output = Mat()
    Imgproc.cvtColor(this, output, Imgproc.COLOR_RGB2GRAY)
    return output
}

fun Mat.resize(): Mat {
    val output = Mat()
    Imgproc.resize(this, output, Size(64.0, 64.0), -1.0, -1.0, Imgproc.INTER_AREA)
    return output
}

fun Mat.normalize(): Mat {
    val output = Mat()
    convertTo(output, CvType.CV_32F)
    Core.divide(output, Scalar(127.5, 127.5, 127.5), output)
    Core.subtract(output, Scalar(1.0, 1.0, 1.0), output)
    return output
}

fun Mat.toBitmap(): Bitmap {
    val input = Mat()
    Imgproc.cvtColor(this, input, Imgproc.COLOR_BGR2RGB)
    return Bitmap.createBitmap(input.cols(), input.rows(), Bitmap.Config.ARGB_8888).apply {
        Utils.matToBitmap(input, this)
    }
}

fun Mat.to4DArray(): Array<Array<Array<FloatArray>>> {
    val result = Array(64) { Array(64) { floatArrayOf() } }
    for (column in 0..63) {
        for (row in 0..63) {
            result[row][column] = floatArrayOf(get(row, column)[0].toFloat())
        }
    }
    return arrayOf(result)
}

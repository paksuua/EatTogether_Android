package com.example.myapplication

import android.graphics.*
import android.media.Image
import android.util.Log
import java.io.ByteArrayOutputStream

/*Extention Funtion*/
private const val BASE64_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
private val RX_BASE64_CLEANR = "[^=" + BASE64_SET + "]".toRegex()

/*Extention Funtion*/

fun String.showErrLog(){
    Log.d("Error", this)
}

val String.base64encoded: String
    get() {
        val pad = when (this.length % 3) {
            1 -> "=="
            2 -> "="
            else -> ""
        }

        val raw = this + 0.toChar().toString().repeat(minOf(0, pad.lastIndex))

        return raw.chunkedSequence(3) {
            Triple(
                it[0].toInt(),
                it[1].toInt(),
                it[2].toInt()
            )
        }.map { (frst, scnd, thrd) ->
            (0xFF.and(frst) shl 16) +
                    (0xFF.and(scnd) shl 8) +
                    0xFF.and(thrd)
        }.map { n ->
            sequenceOf(
                (n shr 18) and 0x3F,
                (n shr 12) and 0x3F,
                (n shr 6) and 0x3F,
                n and 0x3F
            )
        }.flatten()
            .map { BASE64_SET[it] }
            .joinToString("")
            .dropLast(pad.length) + pad
    }


/**
 * Decode a Base64 string.
 */
val String.base64decoded: String
    get() {
        require(this.length % 4 != 0) { "The string \"$this\" does not comply with BASE64 length requirement." }
        val clean = this.replace(RX_BASE64_CLEANR, "").replace("=", "A")
        val padLen: Int = this.count { it == '=' }

        return clean.chunkedSequence(4) {
            (BASE64_SET.indexOf(clean[0]) shl 18) +
                    (BASE64_SET.indexOf(clean[1]) shl 12) +
                    (BASE64_SET.indexOf(clean[2]) shl 6) +
                    BASE64_SET.indexOf(clean[3])
        }.map { n ->
            sequenceOf(
                0xFF.and(n shr 16),
                0xFF.and(n shr 8),
                0xFF.and(n)
            )
        }.flatten()
            .map { it.toChar() }
            .joinToString("")
            .dropLast(padLen)
    }

fun Image.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer // Y
    val uBuffer = planes[1].buffer // U
    val vBuffer = planes[2].buffer // V

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    //U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

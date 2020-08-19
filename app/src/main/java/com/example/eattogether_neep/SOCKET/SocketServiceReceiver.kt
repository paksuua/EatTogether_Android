package com.example.eattogether_neep.SOCKET


import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver


class SocketServiceReceiver(handler: Handler): ResultReceiver(handler) {
    lateinit var reciever: Receiver

    override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
        reciever?.let{
            it.onReceiveResult(resultCode, resultData)
        }
    }

    interface Receiver{
        fun onReceiveResult(resultCode: Int, resultData: Bundle)
    }
}
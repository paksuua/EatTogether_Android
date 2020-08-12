package com.example.eattogether_neep.SOCKET

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.lang.RuntimeException
import java.net.URISyntaxException

class SingleSocket {
    companion object {
        private val TAG = SingleSocket::class.java.simpleName
        private var instance: Socket? = null
        private lateinit var context: Context

        fun getInstance(context: Context): Socket = instance
            ?: synchronized(this) {
                instance ?: try {
                    IO.socket("url")
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }.also {
                    Companion.context = context
                    instance = it
                    instance.apply {
                        this?.on(
                            Socket.EVENT_CONNECT,
                            onConnect
                        )
                        this?.on(
                            Socket.EVENT_RECONNECT,
                            onReconnect
                        )
                        this?.on(
                            Socket.EVENT_DISCONNECT,
                            onDisconnect
                        ) //
                        this?.on(
                            Socket.EVENT_CONNECT_TIMEOUT,
                            onConnctTimeOut
                        ) //
                        this?.on(
                            Socket.EVENT_CONNECT_ERROR,
                            onConnectError
                        ) //
                        this?.on(
                            Socket.EVENT_ERROR,
                            onEventError
                        )
                        this?.on(
                            "joinRoom",
                            onJoinRoom
                        ) //
                        this?.on(
                            Socket.EVENT_PING,
                            onPing
                        ) //
                        instance?.connect()
                        Log.d(TAG, "Socket Send Connect")
                    }
                }
            }

        private val onReconnect: Emitter.Listener = Emitter.Listener { time ->
            Log.d(TAG, "Socket onReconnect (time: $time)")
        }

        private val onConnect: Emitter.Listener = Emitter.Listener {
            Log.d(TAG, "Socket onConnect")
            Log.d(TAG, "Connect Time = ${SystemClock.elapsedRealtime()}")
        }
        private val onDisconnect: Emitter.Listener = Emitter.Listener { reason ->
            val d = Log.d(
                TAG,
                "Socket onDisconnect (reason: ${reason[0]}) (code: ${reason[0].hashCode()}"
            )
            Log.d(TAG, "Connect Time = ${SystemClock.elapsedRealtime()}")
        }
        private val onConnctTimeOut: Emitter.Listener = Emitter.Listener {
            Log.d(TAG, "Socket onConnectTimeOut")
        }

        private val onConnectError: Emitter.Listener = Emitter.Listener {
            Log.d(TAG, "Socket onConnectError it[0]:${it[0]}")
        }
        private val onEventError: Emitter.Listener = Emitter.Listener { error ->
            Log.d(TAG, "Socket AutoMatically onEventError (error: ${error})")
        }

        private val onJoinRoom: Emitter.Listener = Emitter.Listener {
            val token = it[0].toString()
            Log.d(TAG, "Socket onJoinRoom, (token: $token)")
        }


        fun socketDisconnect() {
            instance?.apply {
                this.off(
                    Socket.EVENT_CONNECT,
                    onConnect
                )
                this.off(
                    Socket.EVENT_DISCONNECT,
                    onDisconnect
                )
                this.off(
                    Socket.EVENT_CONNECT_TIMEOUT,
                    onConnctTimeOut
                )
                this.off(
                    Socket.EVENT_CONNECT_ERROR,
                    onConnectError
                )
                this.off(
                    Socket.EVENT_ERROR,
                    onEventError
                )
                this.off(
                    "joinRoom",
                    onJoinRoom
                )

                this.off(
                    Socket.EVENT_PING,
                    onPing
                )
                instance = null
                this.disconnect()

            }
        }

        private val onPing: Emitter.Listener = Emitter.Listener {
            Log.d(TAG, "Socket onPing")
            instance?.emit(Socket.EVENT_PONG)
            Log.d(TAG, "Send Pong")
        }

    }
}
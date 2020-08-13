package com.example.eattogether_neep.Socket

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Base64
import android.util.Log
import android.widget.Toast
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
                    IO.socket(" http://13.125.224.168:8000/ranking")
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
                        ) //
                        this?.on(
                            "createRoom",
                            onCreateRoom
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

        private val onStart: Emitter.Listener = Emitter.Listener {
            Log.d(TAG, "Socket onStart")
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

        private val onCreateRoom: Emitter.Listener = Emitter.Listener {
            val suc = it[0] as Int
            Log.d(TAG, "Socket onCreateRoom Suc: $suc")

            Intent().also { intent ->
                intent.action = "com.example.eattogether_neep.RESULT_ENTERENCE"
                intent.putExtra("suc", suc)
                context.sendBroadcast(intent)
            }
        }

       /* private val onTimeLeft: Emitter.Listener = Emitter.Listener {
            val leftTime = it[0] as Int
            Log.d(TAG, "Socket onTimeLeft LeftTime: $leftTime")

            Intent().also { intent ->
                intent.action = "com.team.runnershi.RESULT_LEFT_TIME"
                intent.putExtra("leftTIme", leftTime)
                context.sendBroadcast(intent)
            }
        }

        private val onEndCount: Emitter.Listener = Emitter.Listener {
            val roomName = it[0].toString()
            Log.d(TAG, "Socket onEndCount (Room Name :$roomName)")
        }

        private val onTimeOver: Emitter.Listener = Emitter.Listener {
            Log.d(TAG, "Socket onTimeOver")
            socketDisconnect()
        }*/

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
                    "start",
                    onStart
                )
                this.off(
                    "createRoom",
                    onCreateRoom
                )
                this.off(
                    Socket.EVENT_PING,
                    onPing
                )
                instance = null
                this.disconnect()

            }
        }

       /* private val onStopCount: Emitter.Listener = Emitter.Listener {
            val leftTime = it[0] as Int
            Log.d(TAG, "Socket onStopCount (leftTime: $leftTime)")
        }

        private val onMatched: Emitter.Listener = Emitter.Listener {
            val roomName = it[0].toString()
            Log.d(TAG, "Socket onMatched (Room Name :$roomName)")
            instance?.emit("endCount", roomName)
            Log.d(TAG, "Send Socket endCount (Room Name :$roomName)")
        }
        private val onRoomFull: Emitter.Listener = Emitter.Listener {
            val roomName = it[0].toString()
            Log.d(TAG, "Socket onRoomFull (roomName: $roomName)")
            instance?.emit("opponentInfo", roomName)
            Log.d(TAG, "Send Socket opponentInfo (roomName: $roomName)")
        }

        private val onOpponentInfo: Emitter.Listener = Emitter.Listener {
            Log.d(TAG, "Socket onOpponenetInfo")
            val roomName = it[0].toString()

            val strBase64UrlDecode = Base64.decode(it[1].toString(), Base64.URL_SAFE)
            val name = String(strBase64UrlDecode)
            val level = it[2] as Int
            val win = it[3] as Int
            val lose = it[4] as Int
            val image = it[5] as Int
            Log.d(
                TAG,
                "(roomName: $roomName) (name: $name) (level: $level)  (win: $win) (lose: $lose) (image: $image)"
            )

            Intent().also { intent ->
                intent.action = "com.team.runnershi.RESULT_OPPONENT_INFO"
                intent.putExtra("roomName", roomName)
                intent.putExtra("name", name)
                intent.putExtra("level", level)
                intent.putExtra("win", win)
                intent.putExtra("lose", lose)
                intent.putExtra("image", image)
                context.sendBroadcast(intent)
            }
        }

        private val onOpponentNotReady: Emitter.Listener = Emitter.Listener {
        }

        private val onLetsRun: Emitter.Listener = Emitter.Listener {
            Log.d(TAG, "Socket onLetsRun")
            Intent().also { intent ->
                intent.action = "com.team.runnershi.RESULT_LETS_RUN"
                context.sendBroadcast(intent)
            }
        }

        private val onKmPassed: Emitter.Listener = Emitter.Listener {
            val opponentKm = it[0] as Int
            Log.d(TAG, "Socket onKmPassed (Opponent Km: $opponentKm")
            // todo 음성 알림 보내기 = TTS
        }

        private val onStopRunning: Emitter.Listener = Emitter.Listener {
            Log.d(TAG, "Socket onStopRunning")
            socketDisconnect()
        }

        private val onOpponentStopped: Emitter.Listener = Emitter.Listener {
            Log.d(TAG, "Socket onOpponentStopped")
        }

        private val onEndRunning: Emitter.Listener = Emitter.Listener {
            val roomName = it[0].toString()
            Log.d(TAG, "Socket onEndRunning (roomName: ${it[0]}")
            Intent().also { intent ->
                intent.action = "com.team.runnershi.RESULT_END_RUNNING"
                intent.putExtra("roomName", roomName)
                context.sendBroadcast(intent)
            }
        }

        private val onCompareResult: Emitter.Listener = Emitter.Listener {
            val gameIdx = it[0] as Int
            val runIdx = it[1] as Int
            Log.d(TAG, "Socket onCompareResult (gameIdx: $gameIdx) (runIdx: $runIdx)")
            Intent().also { intent ->
                intent.action = "com.team.runnershi.RESULT_COMPARE"
                intent.putExtra("gameIdx", gameIdx)
                intent.putExtra("runIdx", runIdx)
                context.sendBroadcast(intent)
            }
            socketDisconnect()
        }*/


        private val onPing: Emitter.Listener = Emitter.Listener {
            Log.d(TAG, "Socket onPing")
            instance?.emit(Socket.EVENT_PONG)
            Log.d(TAG, "Send Pong")
        }

    }
}
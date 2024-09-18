package com.raybit.newvendor.utils

import android.app.Activity
import android.util.Log
import com.raybit.newvendor.data.models.login.LoginResponse

import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject

class SocketManager {
    var socket: Socket? = null
    var activity: Activity? = null

    interface MessageStatus {
        companion object {
            const val NOT_SENT = "NOT_SENT"
            const val SENDING = "SENDING"
            const val SENT = "SENT"
            const val DELIVERED = "DELIVERED"
            const val SEEN = "SEEN"
        }
    }

    companion object {
        // Listen Events
        const val EVENT_ADD_MESSAGE_RESPONSE = "receive_message"
        const val EVENT_TYPING_MESSAGE_RESPONSE = "typing"
        const val LISTENER_READALL = "message-read"
        const val LISTENER_ONLINE_STATUS = "online-status"

        // Emit Events
        const val EVENT_READ_ALL_MESSAGES = "message-read"
        const val EVENT_ADD_MESSAGE = "send_message"
        const val EVENT_TYPING_MESSAGE = "typing"
        const val EVENT_ONLINE_STATUS = "online-status"

        private var INSTANCE: SocketManager? = null

        fun getInstance() = INSTANCE
            ?: synchronized(SocketManager::class.java) {
                INSTANCE ?: SocketManager().also { INSTANCE = it }
            }

        /**
         * Disconnects from current instance and also releases references to it
         * so that a new instance will be created next time.
         * */
        fun destroy() {
            Log.e("Socket", "Destroying socket instance")
            INSTANCE?.disconnect()
            INSTANCE = null
        }
    }

    fun connect(activity: Activity, user: LoginResponse, msgAck: OnMessageReceiver) {
        this.activity = activity
        val options = IO.Options().apply {
            forceNew = true
            reconnection = true
        }
        try {
            socket = IO.socket("http://192.168.100.28:4000?user_id=${user.id}", options)
            socket?.connect()
            socket?.on(Socket.EVENT_CONNECT) { args ->
                Log.e("Socket", "Socket Connected: ${socket?.id()}")
            }
            socket?.on(Socket.EVENT_DISCONNECT) {
                Log.e("Socket", "Socket Disconnected")
            }
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e("Socket", "Socket Connect Error: ${args[0]}")
            }
        } catch (e: Exception) {
            Log.e("Socket", "Error connecting to socket", e)
        }
    }

    fun disconnect() {
        socketOff()
        socket?.off()
        socket?.disconnect()
    }

    fun on(event: String, listener: Emitter.Listener) {
        socket?.on(event, listener)
    }

    fun off(event: String, listener: Emitter.Listener) {
        socket?.off(event, listener)
    }

    fun emit(event: String, args: Any, acknowledge: Ack) {
        socket?.emit(event, args, acknowledge)
    }

    fun emit(event: String, args: Any) {
        socket?.emit(event, args)
    }

    fun sendTyping(arg: JSONObject, msgAck: OnMessageReceiver) {
        socket?.emit(EVENT_TYPING_MESSAGE, arg, Ack { args ->
            msgAck.onMessageReceive(args[0].toString(), EVENT_TYPING_MESSAGE)
        })
    }

    fun sendOnlineStatus(arg: JSONObject, msgAck: OnMessageReceiver) {
        socket?.emit(EVENT_ONLINE_STATUS, arg, Ack { args ->
            msgAck.onMessageReceive(args[0].toString(), EVENT_ONLINE_STATUS)
        })
    }

    fun sendMessage(arg: JSONObject, msgAck: OnMessageReceiver) {
        socket?.emit(EVENT_ADD_MESSAGE, arg, Ack { args ->
            msgAck.onMessageReceive(args[0].toString(), EVENT_ADD_MESSAGE)
        })
    }

    fun readAllMessages(arg: JSONObject, msgAck: OnMessageReceiver) {
        socket?.emit(EVENT_READ_ALL_MESSAGES, arg, Ack { args ->
            msgAck.onMessageReceive(args[0].toString(), EVENT_READ_ALL_MESSAGES)
        })
    }

    // Listen Events
    fun onTyping(msgAck: OnMessageReceiver) {
        socket?.on(EVENT_TYPING_MESSAGE_RESPONSE) { args ->
            msgAck.onMessageReceive(args[0].toString(), EVENT_TYPING_MESSAGE_RESPONSE)
        }
    }

    fun addChatMessageListener(msgAck: OnMessageReceiver) {
        socket?.on(EVENT_ADD_MESSAGE_RESPONSE) { args ->
            activity?.runOnUiThread {
                msgAck.onMessageReceive(args[0].toString(), EVENT_ADD_MESSAGE_RESPONSE)
            }
        }
    }

    fun addChatMessageReadListener(msgAck: OnMessageReceiver) {
        socket?.on(LISTENER_READALL) { args ->
            msgAck.onMessageReceive(args[0].toString(), LISTENER_READALL)
        }
    }

    fun addOnlineStatusListener(msgAck: OnMessageReceiver) {
        socket?.on(LISTENER_ONLINE_STATUS) { args ->
            msgAck.onMessageReceive(args[0].toString(), LISTENER_ONLINE_STATUS)
        }
    }

    private fun socketOff() {
        socket?.off(EVENT_TYPING_MESSAGE_RESPONSE)
        socket?.off(EVENT_ADD_MESSAGE_RESPONSE)
        socket?.off(EVENT_TYPING_MESSAGE_RESPONSE)
        socket?.off(LISTENER_READALL)
        socket?.off(LISTENER_ONLINE_STATUS)
    }

    interface OnMessageReceiver {
        fun onMessageReceive(message: String, event: String)
    }
}

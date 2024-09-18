package com.raybit.newvendor.ui.fragment.chatModule

import android.os.Parcel
import android.os.Parcelable
import com.raybit.newvendor.data.models.chat.ChatMessage

import kotlinx.android.parcel.Parcelize

data class ChatModel(
    var current_page: Int = 0,
    var total_pages: Int = 0,
    var messages: ArrayList<Result>,
    var conversations: ArrayList<Result>
)

data class ChatData(var status: String, var message: String, var data: Result)
data class ChatMessageData(var status: String, var message: String, var data: ChatMessage)

//{"status":"success","message":"Message sent successfully!","data":{"id":6,"sender_id":9,"receiver_id":128,"message":"Hhhhhh"}}
@Parcelize
data class Result(
    var user_id: Int? = null,
    var sender_id: Int? = null,
    var receiver_id: Int? = null,
    var is_typing: Boolean? = null,
    var online_status: Int? = null
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }
}


data class Typing(
    var is_typing: Boolean = false,
    var user_id: Int = 0
)


package com.raybit.newvendor.ui.adapter

import android.app.Activity
import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

import com.raybit.newvendor.utils.ChatType
import com.raybit.newvendor.utils.DateTimeUtils
import com.raybit.newvendor.utils.SocketManager
import com.raybit.newvendor.utils.dateFormatFromMillis
import com.raybit.newvendor.utils.isYesterday
import com.raybit.newvendor.utils.loadImage
import com.raybit.newvendor.utils.viewImageFull
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import com.raybit.newvendor.R
import com.raybit.newvendor.data.models.chat.ChatMessage
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.databinding.ItemChatImageLeftBinding
import com.raybit.newvendor.databinding.ItemChatImageRightBinding
import com.raybit.newvendor.databinding.ItemChatTextLeftBinding
import com.raybit.newvendor.databinding.ItemChatTextRightBinding

import java.util.*

class ChatAdapter(
    private val data: ArrayList<ChatMessage>,
    val mActivity: Context,
    val activity: Activity,
    private val user: LoginResponse
) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val TEXT_LEFT = 0
        private const val TEXT_RIGHT = 1
        private const val IMAGE_LEFT = 2
        private const val IMAGE_RIGHT = 3
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {

            TEXT_RIGHT -> {
                ViewHolderTextRight(
                    DataBindingUtil.inflate(LayoutInflater
                    .from(mActivity), R.layout.item_chat_text_right, viewGroup, false))

            }

            TEXT_LEFT -> {
                ViewHolderTextLeft(
                    DataBindingUtil.inflate(LayoutInflater
                    .from(mActivity),R.layout.item_chat_text_left, viewGroup, false))
            }

            IMAGE_RIGHT -> {
                ViewHolderImageRight(
                    DataBindingUtil.inflate(LayoutInflater
                    .from(mActivity),R.layout.item_chat_image_right, viewGroup, false))
            }

            IMAGE_LEFT -> {
                ViewHolderImageLeft(
                    DataBindingUtil.inflate(LayoutInflater
                    .from(mActivity),R.layout.item_chat_image_left, viewGroup, false))
            }
            else ->  ViewHolderTextRight(
                DataBindingUtil.inflate(LayoutInflater
                .from(mActivity), R.layout.item_chat_text_right, viewGroup, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return if (data[position].receiver_id == user.id) {
            if (data[position].type == ChatType.MESSAGE_TYPE_TEXT) {
                TEXT_LEFT
            } else {
                IMAGE_LEFT
            }
        } else {
            if (data[position].type == ChatType.MESSAGE_TYPE_TEXT) {
                TEXT_RIGHT
            } else {
                IMAGE_RIGHT
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val showDateHeader: Boolean
        if (position == data.size - 1) {
            showDateHeader = true
        } else {
            val cal1 = Calendar.getInstance()
            cal1.timeInMillis = data[position + 1].sentAt ?: 0
            val cal2 = Calendar.getInstance()
            cal2.timeInMillis = data[position].sentAt ?: 0
            showDateHeader = !(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
                Calendar.DAY_OF_YEAR))
        }
        when (holder) {
            is ViewHolderTextRight -> holder.bind(data[position], showDateHeader)
            is ViewHolderTextLeft -> holder.bind(data[position], showDateHeader)
            is ViewHolderImageRight -> holder.bind(data[position], showDateHeader)
            is ViewHolderImageLeft -> holder.bind(data[position], showDateHeader)
        }
    }


    inner class ViewHolderTextRight(val binding: ItemChatTextRightBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatMessage, showDateHeader: Boolean) = with(binding) {
            tvChatTextRight.text = chat.message

            timeRight.text =
                chat.sentAt.let { DateUtils.formatDateTime(binding.root.context, it, DateUtils.FORMAT_SHOW_TIME) }
            if (showDateHeader) {
                tvTimeRight.text = chat.sentAt.let { getDateHeader(it) }
                tvTimeRight.show()
            } else {
                tvTimeRight.hide()
            }
            ivTick.setImageResource(getTickValue(chat.status))
        }


    }

    inner class ViewHolderTextLeft(val binding: ItemChatTextLeftBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatMessage, showDateHeader: Boolean) = with(binding) {
            tvChatTextLeft.text = chat.message
            timeLeft.text =
                chat.sentAt.let { DateUtils.formatDateTime(binding.root.context, it, DateUtils.FORMAT_SHOW_TIME) }
            if (showDateHeader) {
                tvTimeLeft.text = chat.sentAt.let { getDateHeader(it) }
                tvTimeLeft.visibility = View.VISIBLE
            } else {
                tvTimeLeft.visibility = View.GONE
            }
        }

    }

    inner class ViewHolderImageRight(val binding: ItemChatImageRightBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatMessage, showDateHeader: Boolean) = with(binding) {
            mediaTimeRight.text =
                chat.sentAt.let { DateUtils.formatDateTime(binding.root.context, it, DateUtils.FORMAT_SHOW_TIME) }
            if (showDateHeader) {
                tvHeaderMediaRight.text = chat.sentAt.let { getDateHeader(it) }
                tvHeaderMediaRight.visibility = View.VISIBLE
            } else {
                tvHeaderMediaRight.visibility = View.GONE
            }
            ivImageRight.loadImage(chat.file_url)
            /*Click*/
            ivImageRight.setOnClickListener {
                val itemImages = ArrayList<String>()
                itemImages.add(chat.file_url)
                viewImageFull(activity, itemImages, 0)
            }

            ivTick.setImageResource(getTickValue(chat.status))
        }
    }

    inner class ViewHolderImageLeft(val binding: ItemChatImageLeftBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: ChatMessage, showDateHeader: Boolean) = with(binding) {
            mediaTimeLeft.text = chat.sentAt.let { DateUtils.formatDateTime(mActivity, it, DateUtils.FORMAT_SHOW_TIME) }
            if (showDateHeader) {
                tvHeaderMediaLeft.text = chat.sentAt.let { getDateHeader(it) }
                tvHeaderMediaLeft.visibility = View.VISIBLE
            } else {
                tvHeaderMediaLeft.visibility = View.GONE
            }
            ivImageLeft.loadImage(chat.file_url)

            /*Click*/
            ivImageLeft.setOnClickListener {
                val itemImages = ArrayList<String>()
                itemImages.add(chat.file_url)
                viewImageFull( activity, itemImages, 0)
            }
        }
    }

    private fun getDateHeader(millis: Long): String? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val dateString = when {
            DateUtils.isToday(calendar.timeInMillis) -> mActivity.getString(R.string.today)
            isYesterday(calendar) -> String.format("%s", mActivity.getString(R.string.yesterday))
            else -> dateFormatFromMillis(DateTimeUtils.DateFormat.DATE_FORMAT, calendar.timeInMillis)
        }
        return dateString
    }

    private fun getTickValue(status: String?): Int {
        return when (status) {
            SocketManager.MessageStatus.NOT_SENT -> R.drawable.ic_wait
            SocketManager.MessageStatus.SENT -> R.drawable.ic_sent
//            SocketManager.MessageStatus.DELIVERED -> R.drawable.ic_delivered
            SocketManager.MessageStatus.SEEN -> R.drawable.ic_seen
            else -> R.drawable.ic_wait
        }
    }




}

package com.raybit.newvendor.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView


import com.raybit.newvendor.ui.fragment.chatModule.ConversationFragment
import com.raybit.newvendor.utils.loadImage


import com.raybit.newvendor.utils.timeAgo
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import com.raybit.newvendor.R
import com.raybit.newvendor.data.models.FirebaseConversation
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.databinding.LayoutChatUsersListAdapterBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import com.raybit.newvendor.utils.loadImage
import java.util.*

/**
 * Created by Aamir Bashir on 27-11-2021.
 */
class ConversationsAdapter(
    private val fragment: Fragment,
    private val items: ArrayList<FirebaseConversation>,
    var user: LoginResponse
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var context: Context? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position])
//            if(position==0){
//                val params: ViewGroup.LayoutParams = holder.binding.myCard.layoutParams
//                params.height=500
//                params.width = ViewGroup.LayoutParams.MATCH_PARENT
//                holder.binding.myCard.layoutParams = params
//            }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.layout_chat_users_list_adapter, parent, false
            )
        )
    }

    override fun getItemCount(): Int = items.size


    inner class ViewHolder(val binding: LayoutChatUsersListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (fragment is ConversationFragment)
                    fragment.clickItem(items[adapterPosition])
            }
        }

        fun bind(item: FirebaseConversation) = with(binding) {
            val index = if (item?.membersNames?.indexOf(user?.name) != 0) 0 else 1
            binding.tvUsername.text = item.membersNames[index]
            tvLastMessage.text = item.lastMessageSent
            if (item.lastMessageBy != user.id) {
                if (item.read) {
                    imgUnread.hide()
                    mainLayout.setBackgroundColor(context!!.resources.getColor(R.color.white))
                } else {
                    mainLayout.setBackgroundColor(context!!.resources.getColor(R.color.colorSelected))
                    imgUnread.show()
                }
            } else {
                mainLayout.setBackgroundColor(context!!.resources.getColor(R.color.white))
                imgUnread.hide()
            }
            tvTime.text =
                if (item.updatedAt!= "") msgDate(item.updatedAt ?: "")
                else ""
//            tvAddress.text = "${item.user_profile.address}, ${item.user_profile.country}"
//            if (item.user.user_profile.image_url != null) {
//                item.user.user_profile.image_url = item.user.user_profile.image_url.replace(
//                    "http://127.0.0.1:8000/",
//                    "http://192.168.100.21/hospital/public/"
//                )
            binding.ivProfile.loadImage(item.membersProfiles[index], R.drawable.placeholder)
//            }

        }

        private fun msgDate(dateStr: String): String {
            val dateFormat: SimpleDateFormat = if (dateStr.contains("Z")) {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH)
            } else {
                SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
            }

//            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            var format = SimpleDateFormat("yyyy-MM-dd HH:mm a", Locale.ENGLISH)
            val date: Date = format.parse(dateStr)
            dateFormat.timeZone = TimeZone.getDefault()
            val formattedDate = dateFormat.format(date)

            val newDate = (date.time)

            return newDate.timeAgo() ?: ""
        }

    }

    fun formatDate(dateToFormat: String, inputFormat: String?, outputFormat: String?): String? {
        try {
//            Logger.e("DATE", "Input Date Date is $dateToFormat")
            val convertedDate = SimpleDateFormat(outputFormat)
                .format(
                    SimpleDateFormat(inputFormat)
                        .parse(dateToFormat)
                )
//            Logger.e("DATE", "Output Date is $convertedDate")

            //Update Date
            return convertedDate
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }


}

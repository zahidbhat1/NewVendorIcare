package com.raybit.newvendor.ui.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

import com.raybit.newvendor.ui.fragment.home.RequestFragment
import com.raybit.newvendor.utils.CallAction
import com.raybit.newvendor.utils.CallType
import com.raybit.newvendor.utils.DateTimeUtils
import com.raybit.newvendor.utils.LoadingStatus.ITEM
import com.raybit.newvendor.utils.LoadingStatus.LOADING
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.show
import com.raybit.newvendor.R
import com.raybit.newvendor.data.models.ConsultationRequest
import com.raybit.newvendor.databinding.ItemPagingLoaderBinding
import com.raybit.newvendor.databinding.RvItemCallBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi


class RequestAdapter(private val fragment: RequestFragment, private val items: ArrayList<ConsultationRequest>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var allItemsLoaded = true


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType != LOADING)
            (holder as ViewHolder).bind(items[position])
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM) {
            ViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.rv_item_call, parent, false
                    )
            )
        } else {
            ViewHolderLoader(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.item_paging_loader, parent, false
                    )
            )
        }
    }

    override fun getItemCount(): Int = if (allItemsLoaded) items.size else items.size + 1

    override fun getItemViewType(position: Int) = if (position >= items.size) LOADING else ITEM

    @ExperimentalCoroutinesApi
    inner class ViewHolder(val binding: RvItemCallBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {
            binding.tvAccept.setOnClickListener {
                fragment.proceedRequest(items[absoluteAdapterPosition])
            }

            binding.tvCancel.setOnClickListener {
                fragment.cancelAppointment(items[absoluteAdapterPosition])
            }
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(request: ConsultationRequest) = with(binding) {
            val context = binding.root.context

            tvAccept.show()
            tvCancel.hide()
            tvPrice.hide()
            val userProfile = request.user_profile
            if (userProfile != null && userProfile.fullname != null) {
                tvName.text = userProfile.fullname
            } else {
                tvName.text = "NA"
            }
            tvTime.text =   DateTimeUtils.dateTimeFormatFromUTC(DateTimeUtils.DateFormat.TIME_FORMAT, request.created_at)

            when (request.type) {
                CallType.CHAT -> {
                    tvRequestType.text = context.getString(R.string.chat_request)
                    ivRequestType.setImageResource(R.drawable.ico_chat_green)
                }
                CallType.CALL -> {
                    tvRequestType.text = context.getString(R.string.call_equest)
                    ivRequestType.setImageResource(R.drawable.uc_call_phone)
                }
                CallType.VIDEO -> {
                    tvRequestType.text = context.getString(R.string.video_call_equest)
                    ivRequestType.setImageResource(R.drawable.baseline_video_camera_back_24)
                }
            }

           val mins=DateTimeUtils.timeDifferences(request.created_at)

            when (request.status) {
                CallAction.PENDING -> {
                    tvAccept.text = context.getString(R.string.accept_request)
                    tvCancel.show()
                }
                CallAction.ACCEPT -> {
                    tvAccept.text = context.getString(R.string.start_request)
                    tvCancel.show()
                    if(mins<=10){
                        tvAccept.show()
                    }else{
                        tvAccept.hide()
                    }
                }
                CallAction.INPROGRESS -> {
                    tvAccept.text =     context.getString(R.string.start_request)
                    tvCancel.show()

                }

                CallAction.COMPLETED -> {
                    tvRequestType.text = context.getString(R.string.done)

                    tvAccept.hide()
                    tvPrice.show()

//                    val callTime = request.duration?.toLong() ?: 0
//                    tvPrice.text = "${getCurrencey(request.amount)}/${convertSecondsToMinute(callTime)} min."
                }

                CallAction.CANCELED -> {
                    tvRequestType.text = context.getString(R.string.canceled)
                    tvAccept.hide()
                }

                CallAction.FAILED -> {
                    tvRequestType.text = context.getString(R.string.no_show)
                    tvAccept.hide()
                }
                else -> {

                }
            }
        }
    }

    inner class ViewHolderLoader(val binding: ItemPagingLoaderBinding) :
            RecyclerView.ViewHolder(binding.root)

    fun setAllItemsLoaded(allLoaded: Boolean) {
        allItemsLoaded = allLoaded
    }
}

package com.raybit.newvendor.ui.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

import com.raybit.newvendor.ui.fragment.profile.HistoryFragment
import com.raybit.newvendor.R
import com.raybit.newvendor.data.models.wallet.Transaction
import com.raybit.newvendor.databinding.ItemNotificationsBinding
import com.raybit.newvendor.utils.DateTimeUtils.DateFormat.DATE_TIME_FORMAT
import com.raybit.newvendor.utils.DateTimeUtils.dateTimeFormatFromUTC
import com.raybit.newvendor.utils.StaticFunction.getCurrencey

/** Created by Aamir Bashir on 27-11-2021. */
class TransactionsAdapter(
    val fragment: HistoryFragment,
    private val items: ArrayList<Transaction>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(items[position])

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_notifications, parent, false
            )
        )
    }

    override fun getItemCount(): Int = items.size


    inner class ViewHolder(val binding: ItemNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: Transaction) = with(binding) {
            tvReason.text = item.reason
            tvDate.text = dateTimeFormatFromUTC(DATE_TIME_FORMAT,item.created_at)

            if (item.type == "debit") {
                tvPrice.text ="-${getCurrencey(item.amount)}"
                tvPrice.setTextColor(tvPrice.context.resources.getColor(R.color.red))
                cvProfile.setImageDrawable(cvProfile.context.getDrawable(R.drawable.download_line))
                cvProfile.background =
                    cvProfile.context.getDrawable(R.drawable.primary_gradient_new_shape_debit)
            }else{
                tvPrice.text ="+${getCurrencey(item.amount)}"
                tvPrice.setTextColor(tvPrice.context.resources.getColor(R.color.green))
                cvProfile.setImageDrawable(cvProfile.context.getDrawable(R.drawable.upload_line))
                cvProfile.background =
                    cvProfile.context.getDrawable(R.drawable.primary_gradient_new_shape)
            }

        }
    }

}

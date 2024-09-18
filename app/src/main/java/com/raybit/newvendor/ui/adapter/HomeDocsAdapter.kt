package com.raybit.newvendor.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.raybit.newvendor.R
import com.raybit.newvendor.data.models.login.UserProfile
import com.raybit.newvendor.databinding.RvItemDoctorBinding

import com.raybit.newvendor.utils.loadImage

/**
 * Created by Aamir Bashir on 27-11-2021.
 */
class HomeDocsAdapter(
    private val fragment: Fragment,
    private val items: ArrayList<UserProfile>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


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
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rv_item_doctor, parent, false
            )
        )
    }

    override fun getItemCount(): Int = items.size


    inner class ViewHolder(val binding: RvItemDoctorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
//               if (fragment is HomeFragment)
//                   fragment.clickDoctor(items[adapterPosition])
            }
        }

        fun bind(item: UserProfile) = with(binding) {
            tvName.text = item.fullname
            tvDesc.text = item.category.title
            tvAddress.text = "${item.address}, ${item.country}"
            if (item.image_url!=null) {
                item.image_url = item.image_url.replace(
                    "http://127.0.0.1:8000/",
                    "http://192.168.100.21/hospital/public/"
                )
            binding.ivPic.loadImage(item.image_url, R.drawable.error)
            }

        }
    }

}
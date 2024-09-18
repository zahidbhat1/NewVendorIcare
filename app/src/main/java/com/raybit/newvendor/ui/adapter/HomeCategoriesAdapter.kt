package com.raybit.newvendor.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raybit.newvendor.ui.fragment.home.HomeFragment
import com.raybit.newvendor.R
import com.raybit.newvendor.data.models.service.Category
import com.raybit.newvendor.databinding.RvItemCategoryBinding
import com.raybit.newvendor.utils.loadImage

/**
 * Created by Aamir Bashir on 27-11-2021.
 */
class HomeCategoriesAdapter(
    private val fragment: HomeFragment,
    private val items: ArrayList<Category>,
    private val recyclerView: RecyclerView
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {

                if ((position+1)%3==0) {
                    return 2
                } else {
                    return 1
                }
            }
        }
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
                R.layout.rv_item_category, parent, false
            )
        )
    }

    override fun getItemCount(): Int = items.size


    inner class ViewHolder(val binding: RvItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                fragment.clickItem(items[adapterPosition])
            }
        }

        fun bind(item: Category) = with(binding) {
            tvName.text = item.title
            item.banner_url = item.banner_url.replace(
                "http://127.0.0.1:8000/",
                "http://192.168.100.21/hospital/public/"
            )
            binding.ivCategory.loadImage(item.banner_url, R.drawable.error)

        }
    }

}

package com.cmpe451.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentFollowerFollowingItemBinding
import com.cmpe451.platon.network.models.FollowPerson
import com.cmpe451.platon.util.Definitions

class FollowerFollowingAdapter(private val dataSet: ArrayList<FollowPerson>,
                               private val context:Context,
                               private val clickCallback:(Int) ->Unit) : RecyclerView.Adapter<FollowerFollowingAdapter.ViewHolder>() {
    class ViewHolder(val binding: FragmentFollowerFollowingItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model:FollowPerson, context:Context, clickCallback:(Int)->Unit){
            binding.textProfilePageInfoTitle.text = model.name + " " + model.surname

            Glide.with(context)
                    .load(Definitions.API_URL + "api" + model.profile_photo)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .circleCrop()
                    .placeholder(R.drawable.ic_o_logo)
                    .into(binding.profilePageIcon)

            binding.profilePageIcon
            binding.textProfilePageInfoTitle.setOnClickListener{
                clickCallback.invoke(model.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FragmentFollowerFollowingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    fun submitList(list:ArrayList<FollowPerson>){
//        this.dataSet.clear()
        this.dataSet.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position], context, clickCallback)
    }
    /**
     * Clear all elements
     */
    fun clearElements(){
        dataSet.clear()
        this.notifyDataSetChanged()
    }

}
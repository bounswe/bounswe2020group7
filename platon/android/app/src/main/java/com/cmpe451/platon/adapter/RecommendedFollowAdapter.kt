package com.cmpe451.platon.adapter

import com.cmpe451.platon.network.models.RecommendedUser


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

class RecommendedFollowAdapter(private val dataSet: ArrayList<RecommendedUser>,
                               private val context:Context,
                               private val clickCallback:(Int) ->Unit) : RecyclerView.Adapter<RecommendedFollowAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a RecommendedUser in this case
    class ViewHolder(val binding: FragmentFollowerFollowingItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model:RecommendedUser, context:Context, clickCallback:(Int)->Unit){
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

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FragmentFollowerFollowingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // Adds given list of element to dataset
    fun submitList(list:ArrayList<RecommendedUser>){
        this.dataSet.clear()
        this.dataSet.addAll(list)
        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return dataSet.size
    }

    // Replace the contents of a view (invoked by the layout manager)
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
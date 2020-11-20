package com.cmpe451.platon.page.fragment.profilepage.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R

class FollowingRecyclerViewAdapter(private val dataSet: ArrayList<String>) : RecyclerView.Adapter<FollowingRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val view: View = v

        init {
            // Define click listener for the ViewHolder's View.
        }
        fun bind(model:String){
            val nameText :TextView = view.findViewById(R.id.text_profile_page_info_title)
            nameText.text = model
        }
    }    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_following_item, parent, false)
        return FollowingRecyclerViewAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: FollowingRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }
    fun submitList(list:ArrayList<String>){
        this.dataSet.clear()
        this.dataSet.addAll(list)
        notifyDataSetChanged()
    }


}
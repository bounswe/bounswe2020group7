package com.cmpe451.platon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R

class ProfilePageRecyclerViewAdapter(private val dataSet: ArrayList<MutableMap<String, String>>) : RecyclerView.Adapter<ProfilePageRecyclerViewAdapter.ViewHolder>()  {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val view: View = v

        init {
            // Define click listener for the ViewHolder's View.
        }
        fun bind(model:MutableMap<String, String>){
            val title : TextView = view.findViewById(R.id.text_profile_page_info_title)
            title.text = model["title"]
            val detail : TextView = view.findViewById(R.id.text_profile_page_info)
            detail.text = model["info"]
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_profile_page_info_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }
    fun submitList(list:ArrayList<MutableMap<String, String>>){
        this.dataSet.clear()
        this.dataSet.addAll(list)
        notifyDataSetChanged()
    }



}
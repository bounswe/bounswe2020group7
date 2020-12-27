package com.cmpe451.platon.adapter

/**
 * @author Burak Ömür
 */

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.SearchElementCellBinding
import com.cmpe451.platon.network.models.SearchElement
import com.cmpe451.platon.util.Definitions


class SearchElementsAdapter(private val data: ArrayList<SearchElement>, private val context: Context, private val searchButtonClickListener: SearchButtonClickListener) :

        RecyclerView.Adapter<SearchElementsAdapter.MyViewHolder>(),ToolbarElementsAdapter {

    interface SearchButtonClickListener{
        fun onSearchButtonClicked(element: SearchElement, position: Int)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val binding: SearchElementCellBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(element:SearchElement, position: Int, buttonClickListener: SearchButtonClickListener) {
            binding.rlSearchElement.setOnClickListener{
                buttonClickListener.onSearchButtonClicked(element ,position)
            }
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(SearchElementCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        when{
            data[position].name != null ->{
            Glide.with(context).load(Definitions.API_URL + "api" + data[position].profile_photo)
                .placeholder(R.drawable.ic_o_logo)
                .circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.binding.iwSearchImage)
             holder.binding.tvDetail.text = "Privacy: " + if(data[position].is_private == 1) "True" else "False"
            holder.binding.tvSearchElement.text = data[position].name + " " + data[position].surname
        }
            (data[position].state != null) ->{
            holder.binding.tvDetail.text = data[position].creation_time + " / " + data[position].deadline
            holder.binding.tvSearchElement.text = data[position].title
        }
        else ->{
            holder.binding.tvDetail.text  =data[position].date + " / " + data[position].deadline + " / " + data[position].location
            holder.binding.tvSearchElement.text = data[position].acronym
        }
        }
        holder.bindData(data[position], position,searchButtonClickListener)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element:SearchElement){
        data.add(position, element)
        this.notifyItemInserted(position)
    }

    fun submitElements(elements: List<SearchElement>){
        data.addAll(elements)
        this.notifyDataSetChanged()
    }

    /**
     * Removes element at given position
     */
    override fun removeElement(position: Int){
        data.removeAt(position)
        this.notifyDataSetChanged()
    }
    /**
     * Updates element at given position
     */
    fun updateElement(position: Int, element: SearchElement){
        data[position] = element
        this.notifyItemChanged(position)
    }
    /**
     * Clear all elements
     */
    override fun clearElements(){
        data.clear()
        this.notifyDataSetChanged()
    }

    
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
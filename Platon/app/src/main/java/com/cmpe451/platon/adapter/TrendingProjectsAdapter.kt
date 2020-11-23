package com.cmpe451.platon.adapter

/**
 * @author Burak Ömür
 */

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.databinding.TrendProjectCellBinding
import com.cmpe451.platon.util.Definitions.TrendingProject

class TrendingProjectsAdapter(private val data: ArrayList<TrendingProject>, private val context: Context, private val trendingProjectsButtonClickListener: TrendingProjectButtonClickListener) :

        RecyclerView.Adapter<TrendingProjectsAdapter.TrendProjectViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class TrendProjectViewHolder(private val view: View, var binding: TrendProjectCellBinding) : RecyclerView.ViewHolder(view){

        fun bindData(binding: TrendProjectCellBinding, position: Int,buttonClickListener: TrendingProjectButtonClickListener) {

            view.setOnClickListener{
                buttonClickListener.onTrendingProjectButtonClicked(binding,position)
            }
        }
    }

    interface TrendingProjectButtonClickListener{
        fun onTrendingProjectButtonClicked(binding: TrendProjectCellBinding, position: Int)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TrendProjectViewHolder {
        // create a new view
        val binding = TrendProjectCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TrendProjectViewHolder(binding.root, binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TrendProjectViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        val drawable = data[position].img
        drawable?.setBounds(0,0,drawable.intrinsicWidth,drawable.intrinsicHeight)
        holder.binding.descTrendProjectTv.text = data[position].description
        holder.binding.titleTrendProjectTv.text = data[position].project_title
        holder.binding.titleTrendProjectTv.setCompoundDrawables(drawable, null, null, null)
        holder.binding.whyTrendProjectTv.text = data[position].reason.toString()

        holder.bindData(holder.binding, position, trendingProjectsButtonClickListener)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element:TrendingProject){
        data.add(position, element)
        this.notifyItemInserted(position)
    }

    /**
     * Removes element at given position
     */
    fun removeElement(position: Int){
        data.removeAt(position)
        this.notifyItemRemoved(position)
    }
    /**
     * Updates element at given position
     */
    fun updateElement(position: Int, element: TrendingProject){
        data[position] = element
        this.notifyItemChanged(position)
    }
    /**
     * Clear all elements
     */
    fun clearElements(){
        data.clear()
        this.notifyDataSetChanged()
    }
    fun submitElements(list:ArrayList<TrendingProject>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
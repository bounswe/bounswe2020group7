package com.cmpe451.platon.adapter

/**
 * @author Burak Ömür
 */

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.databinding.TrendProjectCellBinding
import com.cmpe451.platon.network.models.TrendingProject
import com.cmpe451.platon.util.Definitions

class TrendingProjectsAdapter(private val data: ArrayList<TrendingProject>, private val context: Context, private val trendingProjectsButtonClickListener: TrendingProjectButtonClickListener) :

        RecyclerView.Adapter<TrendingProjectsAdapter.TrendProjectViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a TrendingProject in this case
    class TrendProjectViewHolder(private val view: View, var binding: TrendProjectCellBinding) : RecyclerView.ViewHolder(view){

        fun bindData(binding: TrendProjectCellBinding, project:TrendingProject,buttonClickListener: TrendingProjectButtonClickListener) {

            view.setOnClickListener{
                buttonClickListener.onTrendingProjectButtonClicked(binding,project)
            }
        }
    }

    // Interface used in order to handle click
    interface TrendingProjectButtonClickListener{
        fun onTrendingProjectButtonClicked(binding: TrendProjectCellBinding, project:TrendingProject)
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

        holder.binding.descTrendProjectTv.text = data[position].description
        holder.binding.titleTrendProjectTv.text = data[position].title
        holder.binding.whyTrendProjectTv.text = "Popular"

        holder.bindData(holder.binding, data[position], trendingProjectsButtonClickListener)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: TrendingProject){
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
    fun updateElement(position: Int, element:TrendingProject){
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

    // Adds given list of element to dataset
    fun submitElements(list: List<TrendingProject>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
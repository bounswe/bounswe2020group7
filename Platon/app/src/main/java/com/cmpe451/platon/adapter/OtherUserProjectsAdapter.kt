package com.cmpe451.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.ResearchesCellBinding
import com.cmpe451.platon.network.models.Research

class OtherUserProjectsAdapter(private val data: ArrayList<Research>, private val context: Context, private val userProjectsButtonClickListener: OtherUserProjectButtonClickListener) :

    RecyclerView.Adapter<OtherUserProjectsAdapter.OtherUserProjectsViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class OtherUserProjectsViewHolder(val binding: ResearchesCellBinding) : RecyclerView.ViewHolder(binding.root){

        fun bindData(binding: ResearchesCellBinding, position: Int, buttonClickListener: OtherUserProjectButtonClickListener) {
            binding.editIv.visibility = View.GONE
            binding.expandRl.setOnClickListener{
                buttonClickListener.onUserProjectButtonClicked(binding,position)
            }

        }
    }

    interface OtherUserProjectButtonClickListener{
        fun onUserProjectButtonClicked(binding: ResearchesCellBinding, position: Int)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherUserProjectsViewHolder {
        return OtherUserProjectsViewHolder(ResearchesCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: OtherUserProjectsViewHolder, position: Int) {
        holder.binding.descTrendProjectTv.text = data[position].description
        holder.binding.titleTrendProjectTv.text = data[position].title
        holder.binding.projectYearTv.text = data[position].year.toString()

        holder.bindData(holder.binding, position, userProjectsButtonClickListener)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: Research){
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
    fun updateElement(position: Int, element: Research){
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
    fun submitElements(list: List<Research>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
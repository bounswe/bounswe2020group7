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

class UserProjectsAdapter(private val data: ArrayList<Research>, private val context: Context, private val userProjectsButtonClickListener: UserProjectButtonClickListener) :

        RecyclerView.Adapter<UserProjectsAdapter.UserProjectsViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class UserProjectsViewHolder(val binding: ResearchesCellBinding) : RecyclerView.ViewHolder(binding.root){

        fun bindData(binding: ResearchesCellBinding, position: Int,buttonClickListener: UserProjectButtonClickListener) {
            binding.expandRl.setOnClickListener{
                buttonClickListener.onUserProjectButtonClicked(binding,position)
            }
            binding.editIv.setOnClickListener{
                buttonClickListener.onUserProjectEditClicked(position)
            }
        }
    }

    interface UserProjectButtonClickListener{
        fun onUserProjectButtonClicked(binding: ResearchesCellBinding, position: Int)
        fun onUserProjectEditClicked(position: Int)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): UserProjectsViewHolder {
        return UserProjectsViewHolder( ResearchesCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: UserProjectsViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //val drawable = data[position].img
        //drawable?.setBounds(0,0,drawable.intrinsicWidth,drawable.intrinsicHeight)
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

    fun getElement(position: Int):Research{
        return data[position]
    }

    /**
     * Clear all elements
     */
    fun clearElements(){
        data.clear()
        this.notifyDataSetChanged()
    }
    fun submitElements(list: List<Research>){
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun replaceElements(list: List<Research>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
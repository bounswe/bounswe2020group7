package com.cmpe451.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmpe451.platon.databinding.ActivityStreamCellBinding
import com.cmpe451.platon.network.models.ActivityStreamElement
import com.cmpe451.platon.util.Definitions

class ActivityStreamAdapter(private val data: ArrayList<ActivityStreamElement>, private val context: Context, private val activityStreamButtonClickListener: ActivityStreamButtonClickListener) :

    RecyclerView.Adapter<ActivityStreamAdapter.ActivityStreamViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ActivityStreamViewHolder(private val view: View, var binding: ActivityStreamCellBinding) : RecyclerView.ViewHolder(view){

        fun bindData(binding: ActivityStreamCellBinding, position: Int,buttonClickListener: ActivityStreamButtonClickListener) {

            view.setOnClickListener{
                buttonClickListener.onActivityStreamButtonClicked(binding,position)
            }
        }
    }

    interface ActivityStreamButtonClickListener{
        fun onActivityStreamButtonClicked(binding: ActivityStreamCellBinding, position: Int)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ActivityStreamViewHolder {
        // create a new view
        val binding = ActivityStreamCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ActivityStreamViewHolder(binding.root, binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ActivityStreamViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.binding.titleActivityStreamCell.text = data[position].message
        Glide.with(context).load(data[position].image).circleCrop().into(holder.binding.activityImg)
        holder.bindData(holder.binding, position, activityStreamButtonClickListener)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: ActivityStreamElement){
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
    fun updateElement(position: Int, element: ActivityStreamElement){
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

    fun submitElements(newList:ArrayList<ActivityStreamElement>){
        data.clear()
        data.addAll(newList)
        this.notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
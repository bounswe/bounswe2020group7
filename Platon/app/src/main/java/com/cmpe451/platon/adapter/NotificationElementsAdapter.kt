package com.cmpe451.platon.adapter

/**
 * @author Burak Ömür
 */

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FollowRequestCellBinding
import com.cmpe451.platon.databinding.SearchElementCellBinding
import com.cmpe451.platon.network.models.Notification


class NotificationElementsAdapter(private val data: ArrayList<Notification>, private val context: Context, private val notificationButtonClickListener: NotificationButtonClickListener) :
        RecyclerView.Adapter<NotificationElementsAdapter.MyViewHolder>(),ToolbarElementsAdapter {



    interface NotificationButtonClickListener{
        fun onNotificationButtonClicked(ntf: Notification, position: Int)
        fun onDeleteNotificationClicked(ntf: Notification, position: Int)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val binding: FollowRequestCellBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(ntf: Notification, buttonClickListener: NotificationButtonClickListener, position: Int) {

            binding.rlFollowRequestCell.setOnClickListener{
                buttonClickListener.onNotificationButtonClicked(ntf, position)
            }
            binding.rejectIcon.setOnClickListener{
                buttonClickListener.onDeleteNotificationClicked(ntf, position)
            }

        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(FollowRequestCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.binding.nameBlock.text = data[position].text
        holder.binding.acceptIcon.visibility = View.GONE
        Glide.with(context).load(data[position].link).circleCrop().into(holder.binding.ivProfilePhoto)
        holder.bindData(data[position], notificationButtonClickListener, position)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element:Notification){
        data.add(position, element)
        this.notifyItemInserted(position)
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
    fun updateElement(position: Int, element: Notification){
        data[position] = element
        this.notifyItemChanged(position)
    }

    fun submitElements(ntf:List<Notification>){
        data.addAll(ntf)
        notifyDataSetChanged()
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
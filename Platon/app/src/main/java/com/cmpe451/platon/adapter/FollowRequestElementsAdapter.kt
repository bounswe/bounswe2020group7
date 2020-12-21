package com.cmpe451.platon.adapter

/**
 * @author Burak Ömür
 */

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FollowRequestCellBinding
import com.cmpe451.platon.network.models.FollowRequest
import com.cmpe451.platon.network.models.Notification

interface ToolbarElementsAdapter{
    fun clearElements()
    fun removeElement(position: Int)
}


class FollowRequestElementsAdapter(private val data: ArrayList<FollowRequest>, private val context: Context, private val followRequestButtonClickListener: FollowRequestButtonClickListener) :
        RecyclerView.Adapter<FollowRequestElementsAdapter.MyViewHolder>(), ToolbarElementsAdapter {

    interface FollowRequestButtonClickListener{
        fun onFollowRequestNameClicked(request: FollowRequest, position: Int)
        fun onFollowRequestAcceptClicked(request: FollowRequest, position: Int)
        fun onFollowRequestRejectClicked(request: FollowRequest, position: Int)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val binding: FollowRequestCellBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(request: FollowRequest, buttonClickListener: FollowRequestButtonClickListener, position: Int) {
            binding.nameBlock.setOnClickListener{
                buttonClickListener.onFollowRequestNameClicked(request, position)
            }
            binding.acceptIcon.setOnClickListener{
                buttonClickListener.onFollowRequestAcceptClicked(request, position)
            }
            binding.acceptIcon.setOnClickListener{
                buttonClickListener.onFollowRequestRejectClicked(request, position)
            }
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        return MyViewHolder(FollowRequestCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.binding.nameBlock.text = data[position].name +  " " + data[position].surname
        holder.bindData(data[position], followRequestButtonClickListener, position)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element:FollowRequest){
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
    fun updateElement(position: Int, element: FollowRequest){
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


    fun submitElements(reqs:List<FollowRequest>){
        data.addAll(reqs)
        notifyDataSetChanged()
    }
    
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
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
    class MyViewHolder(val myView: View) : RecyclerView.ViewHolder(myView){
        var myTextView = myView.findViewById(R.id.name_block) as TextView
        var acceptButton = myView.findViewById(R.id.accept_icon) as ImageView
        var rejectButton = myView.findViewById(R.id.reject_icon) as ImageView
        fun bindData(request: FollowRequest, buttonClickListener: FollowRequestButtonClickListener, position: Int) {
            myTextView.setOnClickListener{
                buttonClickListener.onFollowRequestNameClicked(request, position)
            }
            acceptButton.setOnClickListener{
                buttonClickListener.onFollowRequestAcceptClicked(request, position)
            }
            rejectButton.setOnClickListener{
                buttonClickListener.onFollowRequestRejectClicked(request, position)
            }
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        val textView = MyViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.follow_request_cell, parent, false))
        // set the view's size, margins, paddings and layout parameters
        return textView
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.myTextView.text = data[position].name +  " " + data[position].surname
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
        this.notifyItemRemoved(position)
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

    
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
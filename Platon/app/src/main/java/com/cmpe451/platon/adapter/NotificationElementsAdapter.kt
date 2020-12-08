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
import com.cmpe451.platon.R
import com.cmpe451.platon.network.models.Notification


class NotificationElementsAdapter(private val data: ArrayList<Notification>, private val context: Context, private val notificationButtonClickListener: NotificationButtonClickListener) :
        RecyclerView.Adapter<NotificationElementsAdapter.MyViewHolder>(),ToolbarElementsAdapter {

    interface NotificationButtonClickListener{
        fun onNotificationButtonClicked(ntf: Notification, position: Int)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val myView: View) : RecyclerView.ViewHolder(myView){
        var myTextView = myView.findViewById(R.id.title_trend_project_tv) as TextView

        fun bindData(ntf: Notification, buttonClickListener: NotificationButtonClickListener, position: Int) {
            myView.setOnClickListener{
                buttonClickListener.onNotificationButtonClicked(ntf, position)
            }
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        val textView = MyViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.search_element_cell, parent, false))
        // set the view's size, margins, paddings and layout parameters
        return textView
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.myTextView.text = data[position].text
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
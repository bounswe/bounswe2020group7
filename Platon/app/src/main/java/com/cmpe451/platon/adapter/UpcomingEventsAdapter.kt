package com.cmpe451.platon.adapter

/**
 * @author Burak Ömür
 */

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.databinding.UpcomingEventCellBinding
import com.cmpe451.platon.network.models.UpcomingEvent

class UpcomingEventsAdapter(private val data: ArrayList<UpcomingEvent>, private val context: Context, private val upcomingEventsButtonClickListener: UpcomingButtonClickListener) :


        RecyclerView.Adapter<UpcomingEventsAdapter.UpcomingEventViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class UpcomingEventViewHolder(val view: View, var binding: UpcomingEventCellBinding) : RecyclerView.ViewHolder(view){
        fun bindData(position: Int, buttonClickListener: UpcomingButtonClickListener) {
            view.setOnClickListener{
                buttonClickListener.onUpcomingButtonClicked(binding, position)
            }
        }
    }


    interface UpcomingButtonClickListener{
        fun onUpcomingButtonClicked(binding: UpcomingEventCellBinding, position:Int)
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): UpcomingEventsAdapter.UpcomingEventViewHolder {
        // create a new view
        val binding = UpcomingEventCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // set the view's size, margins, paddings and layout parameters

        return UpcomingEventViewHolder(binding.root, binding)
    }



    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: UpcomingEventsAdapter.UpcomingEventViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.binding.upcomingEventTitle.text = data[position].acronym
        holder.binding.upcomingEventDate.text = data[position].date
        holder.binding.upcomingEventDesc.text = data[position].title
        holder.binding.upcomingEventDeadline.text = "Deadline: " + data[position].deadline
        holder.binding.upcomingEventLink.text = data[position].link
        holder.binding.upcomingEventLocation.text = "Location: " + data[position].location

        holder.bindData(position, upcomingEventsButtonClickListener)
    }

    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: UpcomingEvent){
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
    fun updateElement(position: Int, element: UpcomingEvent){
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
    fun submitList(list:ArrayList<UpcomingEvent>){
//        data.clear()
        data.addAll(list)
        this.notifyDataSetChanged()
    }
    fun replaceList(list:ArrayList<UpcomingEvent>){
        data.clear()
        data.addAll(list)
        this.notifyDataSetChanged()
    }
}
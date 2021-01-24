package com.cmpe451.platon.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.CalendarCellBinding
import com.cmpe451.platon.network.models.CalendarItem

class CalendarAdapter(private val data: ArrayList<CalendarItem>, private val context: Context, private val calendarButtonClickListener: CalendarButtonClickListener) :

    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class CalendarViewHolder(private val view: View, var binding: CalendarCellBinding) : RecyclerView.ViewHolder(view){

        @SuppressLint("ResourceAsColor")
        fun bindData(binding: CalendarCellBinding, data: CalendarItem, buttonClickListener: CalendarButtonClickListener) {
            val card:CardView = view.findViewById(R.id.cell_card)
            binding.titleTv.text = data.title
            binding.deadlineTv.text = data.deadline
            when(data.type){
                1->{
                    binding.typeTv.text = "Workspace"
                    binding.worksapceNameTv.visibility = View.GONE
                }
                2->{
                    binding.typeTv.text = "Milestone"
                    binding.worksapceNameTv.text = "Related Workspace: ${data.workspace_title}"
                    card.setBackgroundColor(card.context.resources.getColor(R.color.secondary_yellow))
                }
                3->{
                    binding.typeTv.text = "Issue"
                    binding.worksapceNameTv.text = "Related Workspace: ${data.workspace_title}"
                    card.setBackgroundColor(card.context.resources.getColor(R.color.very_light_brown))
                }
            }
            binding.titleTv.setOnClickListener {
                buttonClickListener.onCalendarItemClicked(data.workspace_id)
            }
        }
    }

    interface CalendarButtonClickListener{
        fun onCalendarItemClicked(wsId:Int)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        // create a new view
        val binding = CalendarCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CalendarViewHolder(binding.root, binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bindData(holder.binding, data[position], calendarButtonClickListener)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: CalendarItem){
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
    fun updateElement(position: Int, element: CalendarItem){
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
    fun submitElements(list: List<CalendarItem>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
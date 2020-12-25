package com.cmpe451.platon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FileItemBinding
import com.cmpe451.platon.databinding.MilestoneCellBinding
import com.cmpe451.platon.network.models.Milestone

class MilestoneAdapter(private val data: ArrayList<Milestone>, private val milestoneButtonClickListener: MilestoneAdapter.MilestoneButtonClickListener, private val isOwner:Boolean) :

    RecyclerView.Adapter<MilestoneAdapter.MilestoneViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MilestoneViewHolder(val binding: MilestoneCellBinding) : RecyclerView.ViewHolder(binding.root){

        fun bindData(binding: MilestoneCellBinding, milestone: Milestone, buttonClickListener: MilestoneButtonClickListener, isOwner: Boolean) {
            if(!isOwner){
                binding.editMilestoneIv.visibility = View.GONE
            }
            binding.milestoneTitle.setOnClickListener{
                buttonClickListener.onMilestoneNameClicked(binding)
            }
            binding.editMilestoneIv.setOnClickListener {
                buttonClickListener.onEditMilestoneClicked(milestone)
            }
            binding.deleteMilestoneIv.setOnClickListener {
                buttonClickListener.onDeleteMilestoneClicked(milestone)
            }
            binding.milestoneDesc.text = milestone.description
            binding.milestoneDeadline.text = "Deadline: ${milestone.deadline}"
            binding.upcomingEventDate.text = milestone.deadline
            binding.milestoneCreatorName.text = "Created by ${milestone.creator_name} ${milestone.creator_surname}"
        }
    }

    interface MilestoneButtonClickListener{
        fun onMilestoneNameClicked(binding: MilestoneCellBinding)
        fun onEditMilestoneClicked(milestone: Milestone)
        fun onDeleteMilestoneClicked(milestone: Milestone)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MilestoneViewHolder {
        return MilestoneViewHolder( MilestoneCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MilestoneViewHolder, position: Int) {
        holder.bindData(holder.binding, data[position], milestoneButtonClickListener, isOwner)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: Milestone){
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
    fun updateElement(position: Int, element: Milestone){
        data[position] = element
        this.notifyItemChanged(position)
    }

    fun getElement(position: Int): Milestone {
        return data[position]
    }

    /**
     * Clear all elements
     */
    fun clearElements(){
        data.clear()
        this.notifyDataSetChanged()
    }
    fun submitElements(list: List<Milestone>){
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun replaceElements(list: List<Milestone>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
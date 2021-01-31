package com.cmpe451.platon.adapter

/**
 * @author Burak Ömür
 */

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.databinding.NotificationElementCellBinding
import com.cmpe451.platon.network.models.WorkspaceApplication


class WorkspaceApplicationsAdapter(private val data: ArrayList<WorkspaceApplication>, private val context: Context, private val invitationButtonClickListener: ApplicationsButtonClickListener) :
        RecyclerView.Adapter<WorkspaceApplicationsAdapter.MyViewHolder>(), ToolbarElementsAdapter {

    // Interface used in order to handle click
    interface ApplicationsButtonClickListener{
        fun onApplicationAcceptClicked(request: WorkspaceApplication, position: Int)
        fun onApplicationRejectClicked(request: WorkspaceApplication, position: Int)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a WorkspaceApplication in this case
    class MyViewHolder(val binding: NotificationElementCellBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(request: WorkspaceApplication, buttonClickListener: ApplicationsButtonClickListener, position: Int) {
            binding.acceptIcon.setOnClickListener{
                buttonClickListener.onApplicationAcceptClicked(request, position)
            }
            binding.rejectIcon.setOnClickListener{
                buttonClickListener.onApplicationRejectClicked(request, position)
            }
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        return MyViewHolder(NotificationElementCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.binding.tvDateTime.visibility  =View.GONE
        holder.binding.nameBlock.text = data[position].applicant_fullname

        holder.bindData(data[position], invitationButtonClickListener, position)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element:WorkspaceApplication){
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
    fun updateElement(position: Int, element: WorkspaceApplication){
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

    // Adds given list of element to dataset
    fun submitElements(reqs:List<WorkspaceApplication>){
        data.addAll(reqs)
        notifyDataSetChanged()
    }
    
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
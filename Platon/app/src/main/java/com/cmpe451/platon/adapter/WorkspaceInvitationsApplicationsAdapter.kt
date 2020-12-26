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
import com.cmpe451.platon.network.models.WorkspaceInvitation
import com.cmpe451.platon.page.activity.home.HomeActivity



class WorkspaceInvitationsApplicationsAdapter(private val data: ArrayList<WorkspaceInvitation>, private val context: Context, private val invitationButtonClickListener: InvitationButtonClickListener) :
        RecyclerView.Adapter<WorkspaceInvitationsApplicationsAdapter.MyViewHolder>(), ToolbarElementsAdapter {

    interface InvitationButtonClickListener{
        fun onInvitationAcceptClicked(request: WorkspaceInvitation, position: Int)
        fun onInvitationRejectClicked(request: WorkspaceInvitation, position: Int)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val binding: NotificationElementCellBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(request: WorkspaceInvitation, buttonClickListener: InvitationButtonClickListener, position: Int) {
            binding.acceptIcon.setOnClickListener{
                buttonClickListener.onInvitationAcceptClicked(request, position)
            }
            binding.rejectIcon.setOnClickListener{
                buttonClickListener.onInvitationRejectClicked(request, position)
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
        holder.binding.nameBlock.text = data[position].description
        holder.bindData(data[position], invitationButtonClickListener, position)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element:WorkspaceInvitation){
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
    fun updateElement(position: Int, element: WorkspaceInvitation){
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


    fun submitElements(reqs:List<WorkspaceInvitation>){
        data.addAll(reqs)
        notifyDataSetChanged()
    }
    
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
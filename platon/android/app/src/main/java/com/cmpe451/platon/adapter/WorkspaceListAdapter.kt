package com.cmpe451.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.WorkspaceCellBinding
import com.cmpe451.platon.network.models.WorkspaceListItem

class WorkspaceListAdapter(private val data: ArrayList<WorkspaceListItem>, private val context: Context, private val workspaceListButtonClickListener: WorkspaceListButtonClickListener) :

    RecyclerView.Adapter<WorkspaceListAdapter.WorkspaceListViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a WorkspaceListItem in this case
    class WorkspaceListViewHolder(private val view: View, var binding: WorkspaceCellBinding) : RecyclerView.ViewHolder(view){

        fun bindData(binding: WorkspaceCellBinding, projectId: Int,buttonClickListener: WorkspaceListButtonClickListener) {
            val expandRl : RelativeLayout = view.findViewById(R.id.expand_rl)
            expandRl.setOnClickListener{
                buttonClickListener.onWorkspaceListButtonClicked(binding,projectId)
            }
        }
    }

    // Interface used in order to handle click
    interface WorkspaceListButtonClickListener{
        fun onWorkspaceListButtonClicked(binding: WorkspaceCellBinding, projectId: Int)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkspaceListViewHolder {
        // create a new view
        val binding = WorkspaceCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return WorkspaceListViewHolder(binding.root, binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: WorkspaceListViewHolder, position: Int) {
        holder.binding.descWorkspaceTv.text = data[position].description
        holder.binding.titleWorkspaceTv.text = data[position].title
        holder.binding.workspaceStateTv.text = when(data[position].state){
            0-> "Search for Collaborators State"
            1-> "Ongoing State"
            else -> "Finished"
        }

        holder.bindData(holder.binding, data[position].id, workspaceListButtonClickListener)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: WorkspaceListItem){
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
    fun updateElement(position: Int, element: WorkspaceListItem){
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

    // Adds given list of element to dataset
    fun submitElements(list: List<WorkspaceListItem>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
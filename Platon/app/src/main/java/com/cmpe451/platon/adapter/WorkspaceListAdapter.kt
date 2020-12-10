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
import com.cmpe451.platon.network.models.Workspace

class WorkspaceListAdapter(private val data: ArrayList<Workspace>, private val context: Context, private val workspaceListButtonClickListener: WorkspaceListButtonClickListener) :

    RecyclerView.Adapter<WorkspaceListAdapter.WorkspaceListViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class WorkspaceListViewHolder(private val view: View, var binding: WorkspaceCellBinding) : RecyclerView.ViewHolder(view){

        fun bindData(binding: WorkspaceCellBinding, position: Int,buttonClickListener: WorkspaceListButtonClickListener) {
            val expandRl : RelativeLayout = view.findViewById(R.id.expand_rl)
            val editImg : ImageView = view.findViewById(R.id.edit_iv)
            expandRl.setOnClickListener{
                buttonClickListener.onWorkspaceListButtonClicked(binding,position)
            }
            editImg.setOnClickListener{
                buttonClickListener.onWorkspaceListEditClicked(position)
            }
        }
    }

    interface WorkspaceListButtonClickListener{
        fun onWorkspaceListButtonClicked(binding: WorkspaceCellBinding, position: Int)
        fun onWorkspaceListEditClicked(position: Int)
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
        holder.binding.workspaceStateTv.text = data[position].state

        holder.bindData(holder.binding, position, workspaceListButtonClickListener)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: Workspace){
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
    fun updateElement(position: Int, element: Workspace){
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
    fun submitElements(list: List<Workspace>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
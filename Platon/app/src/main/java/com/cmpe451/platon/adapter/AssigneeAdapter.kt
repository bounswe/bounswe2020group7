package com.cmpe451.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.databinding.AssigneeCellBinding
import com.cmpe451.platon.network.models.Assignee


class AssigneeAdapter(private val data: ArrayList<Assignee>, private val context: Context) :

    RecyclerView.Adapter<AssigneeAdapter.AssigneeViewHolder>() {
    class AssigneeViewHolder(private val view: View, var binding: AssigneeCellBinding) : RecyclerView.ViewHolder(view){
        fun bindData(binding: AssigneeCellBinding, model:Assignee, position: Int) {
            binding.profilePageIcon.visibility = View.GONE
            binding.textProfilePageInfoTitle.text = model.assignee_name + " " + model.assignee_surname
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssigneeViewHolder {
        // create a new view
        val binding = AssigneeCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssigneeViewHolder(binding.root, binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: AssigneeViewHolder, position: Int) {
        holder.bindData(holder.binding, data[position], position)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: Assignee){
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
    fun updateElement(position: Int, element: Assignee){
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
    fun submitElements(list: List<Assignee>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun getAllElements():ArrayList<Assignee>{
        return data
    }

    override fun getItemCount() = data.size
}
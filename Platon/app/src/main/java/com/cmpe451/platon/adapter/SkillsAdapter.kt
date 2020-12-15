package com.cmpe451.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.databinding.SkillCellPersonalBinding

class SkillsAdapter(private val data: ArrayList<String>, private val context: Context) :

    RecyclerView.Adapter<SkillsAdapter.SkillsViewHolder>() {
    class SkillsViewHolder(private val view: View, var binding: SkillCellPersonalBinding) : RecyclerView.ViewHolder(view){
        fun bindData(binding: SkillCellPersonalBinding, model:String, position: Int) {
            binding.titleSkill.text = model
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillsViewHolder {
        // create a new view
        val binding = SkillCellPersonalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillsViewHolder(binding.root, binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: SkillsViewHolder, position: Int) {
        holder.bindData(holder.binding, data[position], position)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: String){
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
    fun updateElement(position: Int, element: String){
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
    fun submitElements(list: List<String>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun getAllElements():ArrayList<String>{
        return data
    }

    override fun getItemCount() = data.size
}
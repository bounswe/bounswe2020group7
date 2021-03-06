package com.cmpe451.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.databinding.SkillCellPersonalBinding

class SkillsAdapter(private val data: ArrayList<String>,
                    private val context: Context, private val onTagClickedListener: OnTagClickedListener) :
    RecyclerView.Adapter<SkillsAdapter.SkillsViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case
    class SkillsViewHolder(private val view: View, var binding: SkillCellPersonalBinding) : RecyclerView.ViewHolder(view){
        fun bindData(binding: SkillCellPersonalBinding, model:String, position: Int, onTagClickedListener: OnTagClickedListener) {
            binding.titleSkill.text = model

            binding.titleSkill.setOnClickListener{
                onTagClickedListener.onTagClicked(model, position)
            }
        }

    }

    // Interface used in order to handle click
    interface OnTagClickedListener{
        fun onTagClicked(model: String, position: Int)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillsViewHolder {
        // create a new view
        val binding = SkillCellPersonalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillsViewHolder(binding.root, binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: SkillsViewHolder, position: Int) {
        holder.bindData(holder.binding, data[position], position,onTagClickedListener)
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

    // Adds given list of element to dataset
    fun submitElements(list: List<String>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    // Returns elements of the dataset
    fun getAllElements():ArrayList<String>{
        return data
    }

    // Returns element size of the dataset
    override fun getItemCount() = data.size
}
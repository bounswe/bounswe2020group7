package com.cmpe451.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.databinding.FragmentFollowerFollowingItemBinding
import com.cmpe451.platon.databinding.SkillCellPersonalBinding
import com.cmpe451.platon.network.models.Contributor
import com.cmpe451.platon.network.models.SearchElement

class CollaboratorAdapter(private val data: ArrayList<Contributor>, private val context: Context) :

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    RecyclerView.Adapter<CollaboratorAdapter.CollaboratorViewHolder>() {
    class CollaboratorViewHolder(private val view: View, var binding: FragmentFollowerFollowingItemBinding) : RecyclerView.ViewHolder(view){
        fun bindData(binding: FragmentFollowerFollowingItemBinding, model:Contributor, position: Int) {
            binding.profilePageIcon.visibility = View.GONE
            binding.textProfilePageInfoTitle.text = model.name + " " + model.surname
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollaboratorViewHolder {
        // create a new view
        val binding = FragmentFollowerFollowingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollaboratorViewHolder(binding.root, binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: CollaboratorViewHolder, position: Int) {
        holder.bindData(holder.binding, data[position], position)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: Contributor){
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
    fun updateElement(position: Int, element: Contributor){
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

    // Adds a list of element to dataset
    fun submitElements(list: List<Contributor>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    // Returns elements of the dataset
    fun getAllElements():ArrayList<Contributor>{
        return data
    }

    // Returns element size of the dataset.
    override fun getItemCount() = data.size
}
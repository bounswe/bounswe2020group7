package com.cmpe451.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.UserProjectsCellBinding
import com.cmpe451.platon.networkmodels.models.Research

class OtherUserProjectsAdapter(private val data: ArrayList<Research>, private val context: Context, private val userProjectsButtonClickListener: OtherUserProjectButtonClickListener) :

    RecyclerView.Adapter<OtherUserProjectsAdapter.OtherUserProjectsViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class OtherUserProjectsViewHolder(private val view: View, var binding: UserProjectsCellBinding) : RecyclerView.ViewHolder(view){

        fun bindData(binding: UserProjectsCellBinding, position: Int, buttonClickListener: OtherUserProjectButtonClickListener) {
            val expandRl : RelativeLayout = view.findViewById(R.id.expand_rl)
            val editImg : ImageView = view.findViewById(R.id.edit_iv)
            editImg.visibility = View.GONE
            expandRl.setOnClickListener{
                buttonClickListener.onUserProjectButtonClicked(binding,position)
            }

        }
    }

    interface OtherUserProjectButtonClickListener{
        fun onUserProjectButtonClicked(binding: UserProjectsCellBinding, position: Int)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherUserProjectsViewHolder {
        // create a new view
        val binding = UserProjectsCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return OtherUserProjectsViewHolder(binding.root, binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: OtherUserProjectsViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //val drawable = data[position].img
        //drawable?.setBounds(0,0,drawable.intrinsicWidth,drawable.intrinsicHeight)
        holder.binding.descTrendProjectTv.text = data[position].description
        holder.binding.titleTrendProjectTv.text = data[position].title
        //holder.binding.titleTrendProjectTv.setCompoundDrawables(drawable, null, null, null)
        holder.binding.projectYearTv.text = data[position].year.toString()

        holder.bindData(holder.binding, position, userProjectsButtonClickListener)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: Research){
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
    fun updateElement(position: Int, element: Research){
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
    fun submitElements(list: List<Research>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
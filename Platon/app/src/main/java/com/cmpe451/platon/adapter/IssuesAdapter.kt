package com.cmpe451.platon.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmpe451.platon.databinding.IssueCellBinding
import com.cmpe451.platon.network.models.Issue
import com.cmpe451.platon.util.Definitions

class IssuesAdapter(private val data: ArrayList<Issue>, private val context: Context, private val issuesButtonClickListener: IssuesButtonClickListener) :

    RecyclerView.Adapter<IssuesAdapter.IssuesViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class IssuesViewHolder(private val view: View, var binding: IssueCellBinding) : RecyclerView.ViewHolder(view){

        fun bindData(binding: IssueCellBinding, position: Int,buttonClickListener: IssuesButtonClickListener) {

            view.setOnClickListener{
                buttonClickListener.onIssueButtonClicked(binding,position)
            }
        }
    }

    interface IssuesButtonClickListener{
        fun onIssueButtonClicked(binding: IssueCellBinding, position: Int)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): IssuesViewHolder {
        // create a new view
        val binding = IssueCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return IssuesViewHolder(binding.root, binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: IssuesViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.binding.issueTitle.text = data[position].title
        holder.binding.issueDescription.text = data[position].description
        holder.binding.issueDeadline.text = data[position].deadline.toString()

        holder.bindData(holder.binding, position, issuesButtonClickListener)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: Issue){
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
    fun updateElement(position: Int, element: Issue){
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

    fun submitElements(newList:ArrayList<Issue>){
        data.clear()
        data.addAll(newList)
        this.notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
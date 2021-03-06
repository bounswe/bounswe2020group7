package com.cmpe451.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.databinding.IssueCommentCellBinding
import com.cmpe451.platon.network.models.IssueComment
import com.cmpe451.platon.page.activity.workspace.fragment.issuedetail.IssueDetailFragment


class IssueCommentAdapter(private val data: ArrayList<IssueComment>, private val context: Context, private val onCommentClicked: OnCommentClickedListener, private val ownerId:Int) :

    RecyclerView.Adapter<IssueCommentAdapter.IssueCommentsViewHolder>() {

    // Interface used in order to handle click
    interface OnCommentClickedListener {
        fun onDeleteCommentClicked(element: IssueComment, position: Int)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a IssueComment in this case
    class IssueCommentsViewHolder(var binding: IssueCommentCellBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(model:IssueComment, position: Int) {

            binding.issueCommentOwner.text = model.owner_name + " " + model.owner_surname
            binding.issueCommentText.text = model.comment

        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueCommentsViewHolder {
        // create a new view
        val binding = IssueCommentCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IssueCommentsViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: IssueCommentsViewHolder, position: Int) {

        holder.binding.issueDeleteComment.setOnClickListener{
            onCommentClicked.onDeleteCommentClicked(data[position], position)
        }
        if(ownerId == data[position].owner_id){
            holder.binding.issueDeleteComment.visibility = View.VISIBLE
        }else{
            holder.binding.issueDeleteComment.visibility = View.GONE
        }
        holder.bindData(data[position], position)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: IssueComment){
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
    fun updateElement(position: Int, element: IssueComment){
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
    fun submitElements(list: List<IssueComment>){
        data.addAll(list)
        notifyDataSetChanged()
    }

    // Returns elements of the dataset
    fun getAllElements():ArrayList<IssueComment>{
        return data
    }

    // Returns element size of the dataset
    override fun getItemCount() = data.size

}

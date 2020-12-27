package com.cmpe451.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.databinding.CommentCellBinding
import com.cmpe451.platon.network.models.Comment



class CommentsAdapter(private val data: ArrayList<Comment>, private val context: Context, private val onCommentClicked:OnCommentClickedListener, private val ownerId:Int) :

    RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {

    interface OnCommentClickedListener {
        fun onDeleteCommentClicked(element:Comment, position: Int)
    }

    class CommentsViewHolder(var binding: CommentCellBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindData(model:Comment, position: Int) {

            binding.rtbRating.rating = model.rate.toFloat()
            binding.tvDateTime.text = model.timestamp
            binding.tvTitle.text = "Comment: $position"
            binding.tvDescription.text = model.text
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        // create a new view
        val binding = CommentCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentsViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.binding.ivDeleteComment.setOnClickListener{
            onCommentClicked.onDeleteCommentClicked(data[position], position)
        }
        if(ownerId == data[position].owner_id){
            holder.binding.ivDeleteComment.visibility = View.VISIBLE
        }else{
            holder.binding.ivDeleteComment.visibility = View.GONE
        }
        holder.bindData(data[position], position)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: Comment){
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
    fun updateElement(position: Int, element: Comment){
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
    fun submitElements(list: List<Comment>){
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun getAllElements():ArrayList<Comment>{
        return data
    }

    override fun getItemCount() = data.size
}
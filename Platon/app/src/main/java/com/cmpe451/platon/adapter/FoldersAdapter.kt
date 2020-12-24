package com.cmpe451.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.databinding.FolderItemBinding
import com.cmpe451.platon.databinding.ResearchesCellBinding

class FoldersAdapter(private val data: ArrayList<String>, private val context: Context, private val foldersButtonClickListener: FoldersButtonClickListener) :

    RecyclerView.Adapter<FoldersAdapter.FoldersViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class FoldersViewHolder(val binding: FolderItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bindData(binding: FolderItemBinding, folder: String, buttonClickListener: FoldersButtonClickListener) {
            binding.editFileIv.setOnClickListener{
                buttonClickListener.onEditFolderClicked(folder)
            }
            binding.tvFolderName.setOnClickListener {
                buttonClickListener.onFolderNameClicked(folder)
            }
            binding.tvFolderName.text = folder
        }
    }

    interface FoldersButtonClickListener{
        fun onEditFolderClicked(folder:String)
        fun onFolderNameClicked(folder: String)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoldersViewHolder {
        return FoldersViewHolder( FolderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: FoldersViewHolder, position: Int) {


        holder.bindData(holder.binding, data[position], foldersButtonClickListener)
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

    fun getElement(position: Int): String {
        return data[position]
    }

    /**
     * Clear all elements
     */
    fun clearElements(){
        data.clear()
        this.notifyDataSetChanged()
    }
    fun submitElements(list: List<String>){
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun replaceElements(list: List<String>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
package com.cmpe451.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.databinding.FileItemBinding
import com.cmpe451.platon.databinding.FolderItemBinding
import com.cmpe451.platon.databinding.ResearchesCellBinding

class FilesAdapter(private val data: ArrayList<String>, private val filesButtonClickListener: FilesButtonClickListener, private val isOwner:Boolean) :

    RecyclerView.Adapter<FilesAdapter.FilesViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class FilesViewHolder(val binding: FileItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bindData(binding: FileItemBinding, file: String, buttonClickListener: FilesButtonClickListener, isOwner: Boolean) {
            if(!isOwner){
                //binding.editFileIv.visibility = View.GONE
                binding.deleteFolderIv.visibility = View.GONE
            }
            //binding.editFileIv.setOnClickListener{
                //buttonClickListener.onEditFileClicked(file)
            //}
            binding.tvFileName.setOnClickListener {
                buttonClickListener.onFileNameClicked(file)
            }
            binding.deleteFolderIv.setOnClickListener {
                buttonClickListener.onDeleteFileClicked(file)
            }
            binding.tvFileName.text = file
        }
    }

    interface FilesButtonClickListener{
        fun onEditFileClicked(fileName:String)
        fun onFileNameClicked(fileName: String)
        fun onDeleteFileClicked(fileName: String)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilesViewHolder {
        return FilesViewHolder( FileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: FilesViewHolder, position: Int) {
        holder.bindData(holder.binding, data[position], filesButtonClickListener, isOwner)
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
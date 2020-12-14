package com.cmpe451.platon.adapter

/**
 * @author Burak Ömür
 */

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R


class SearchElementsAdapter(private val data: ArrayList<String>, private val context: Context, private val searchButtonClickListener: SearchButtonClickListener) :

        RecyclerView.Adapter<SearchElementsAdapter.MyViewHolder>(),ToolbarElementsAdapter {

    interface SearchButtonClickListener{
        fun onSearchButtonClicked(buttonName: String)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val myView: View) : RecyclerView.ViewHolder(myView){
        var myTextView = myView.findViewById(R.id.title_trend_project_tv) as TextView

        fun bindData(buttonName: String, buttonClickListener: SearchButtonClickListener) {
            myView.setOnClickListener{
                buttonClickListener.onSearchButtonClicked(buttonName)
            }
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        val textView = MyViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.search_element_cell, parent, false))
        // set the view's size, margins, paddings and layout parameters
        return textView
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.myTextView.text = "Searched: " + data[position].toString()
        holder.bindData(data[position].toString(), searchButtonClickListener)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element:String){
        data.add(position, element)
        this.notifyItemInserted(position)
    }

    fun submitElements(elements: List<String>){
        data.addAll(elements)
        this.notifyDataSetChanged()
    }

    /**
     * Removes element at given position
     */
    override fun removeElement(position: Int){
        data.removeAt(position)
        this.notifyDataSetChanged()
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
    override fun clearElements(){
        data.clear()
        this.notifyDataSetChanged()
    }

    
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
package com.example.platon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.platon.R

class UpcomingEventsAdapter(private val myDataset: Array<Int>, private val context: Context, private val upcomingEventsButtonClickListener: UpcomingButtonClickListener) :


        RecyclerView.Adapter<UpcomingEventsAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val myView: View) : RecyclerView.ViewHolder(myView){
        var myTextView = myView.findViewById(R.id.textView) as TextView

        fun bindData(buttonName : String, buttonClickListener: UpcomingButtonClickListener) {
            myView.setOnClickListener{
                buttonClickListener.onUpcomingButtonClicked(buttonName)
            }
        }
    }


    interface UpcomingButtonClickListener{
        fun onUpcomingButtonClicked(buttonName: String)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): UpcomingEventsAdapter.MyViewHolder {
        // create a new view
        val textView = MyViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.upcoming_event_cell, parent, false)
        )
        // set the view's size, margins, paddings and layout parameters

        return textView
    }



    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: UpcomingEventsAdapter.MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.myTextView.text = "Upcoming event" + myDataset[position].toString()
        holder.bindData(myDataset[position].toString(), upcomingEventsButtonClickListener)
    }
}
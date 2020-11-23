package com.cmpe451.platon.adapter

import android.telecom.Call
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentFollowerFollowingItemBinding
import com.cmpe451.platon.networkmodels.ResearchResponse
import com.cmpe451.platon.page.fragment.profilepage.contract.ProfilePageContract
import com.cmpe451.platon.util.Definitions

class FollowerFollowingRecyclerViewAdapter(private val dataSet: ArrayList<Definitions.User>, val clickCallback:(Int) ->Unit) : RecyclerView.Adapter<FollowerFollowingRecyclerViewAdapter.ViewHolder>(),  ProfilePageContract.View {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val view: View = v

        init {
            // Define click listener for the ViewHolder's View.
        }
        fun bind(model:Definitions.User, view:View, clickCallback:(Int)->Unit){

            val nameText :TextView = view.findViewById(R.id.text_profile_page_info_title)
            nameText.text = model.name + " " + model.surname
            nameText.setOnClickListener{
                clickCallback.invoke(model.id)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_follower_following_item, parent, false)
        return ViewHolder(view)
    }
    fun submitList(list:ArrayList<Definitions.User>){
        this.dataSet.clear()
        this.dataSet.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position], holder.view, clickCallback)

    }

    override fun researchesFetched(researchInfo: ResearchResponse) {
        TODO("Not yet implemented")
    }

    override fun initializePresenter() {
        TODO("Not yet implemented")
    }

}
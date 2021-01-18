package com.cmpe451.platon.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.RecommendedProfileCellBinding
import com.cmpe451.platon.network.models.RecommendedUser
import com.cmpe451.platon.util.Definitions

class RecommendedCollaboratorsAdapter(private val data: ArrayList<RecommendedUser>, private val context: Context, private val buttonClickListener: RecommendedUserClickListener, private val canInvite:Boolean) :

    RecyclerView.Adapter<RecommendedCollaboratorsAdapter.RecommendedUserViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class RecommendedUserViewHolder(val binding: RecommendedProfileCellBinding) : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun bindData(binding: RecommendedProfileCellBinding,context: Context, model: RecommendedUser, buttonClickListener: RecommendedUserClickListener, position: Int, canInvite: Boolean) {
            binding.tvNameSurname.text = "${model.name} ${model.surname}"
            binding.tvJob.text = model.job
            binding.tvInstitution.text = model.institution
            Glide.with(context)
                .load(Definitions.API_URL + "api" + model.profile_photo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .placeholder(R.drawable.ic_o_logo)
                .into(binding.profilePageIcon)
            binding.profilePageIcon
            if(canInvite){
                binding.ivInvite.setOnClickListener {
                    buttonClickListener.onInviteUserClicked(model, position)
                }
            }
            else {
                binding.ivInvite.visibility = View.GONE
            }

        }
    }

    interface RecommendedUserClickListener{
        fun onInviteUserClicked(user:RecommendedUser, position: Int)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendedUserViewHolder {
        return RecommendedUserViewHolder( RecommendedProfileCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecommendedUserViewHolder, position: Int) {
        holder.bindData(holder.binding, context, data[position], buttonClickListener, position, canInvite)
    }


    /**
     * Adds element to given position
     */
    fun addElement(position: Int, element: RecommendedUser){
        data.add(position, element)
        this.notifyItemInserted(position)
    }

    /**
     * Removes element at given position
     */
    fun removeElement(position: Int){
        data.removeAt(position)
        this.notifyDataSetChanged()
    }
    /**
     * Updates element at given position
     */
    fun updateElement(position: Int, element: RecommendedUser){
        data[position] = element
        this.notifyItemChanged(position)
    }

    fun getElement(position: Int): RecommendedUser {
        return data[position]
    }

    /**
     * Clear all elements
     */
    fun clearElements(){
        data.clear()
        this.notifyDataSetChanged()
    }
    fun submitElements(list: List<RecommendedUser>){
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun replaceElements(list: List<RecommendedUser>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }


    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = data.size
}
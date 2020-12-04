package com.cmpe451.platon.page.fragment.researchInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.databinding.FragmentResearchInfoEditBinding
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.profilepage.ProfilePageViewModel

class EditResearchInfoFragment : Fragment() {
    private lateinit var binding: FragmentResearchInfoEditBinding
    private val mReseachInfoViewModel: ResearchInfoViewModel by viewModels()
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentResearchInfoEditBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        setListeners()
    }

    private fun setObserver() {
        mReseachInfoViewModel.currentResearch.observe(viewLifecycleOwner) { t ->
            if(t != null){
                binding.projectNameTv.setText(t.title)
                binding.projectYearTv.setText(t.year.toString())
                if(!t.description.isNullOrEmpty()){
                    binding.projectDescriptionTv.setText(t.description)
                }
            }
        }
    }

    private fun setListeners() {
        binding.buttonEdit.setOnClickListener {
            // check if the title and year is empty
            if(binding.projectNameTv.text.isNullOrEmpty() && binding.projectYearTv.text.isNullOrEmpty()){
                Toast.makeText(activity as HomeActivity, "Title and Year cannot be left empty", Toast.LENGTH_LONG)
            }
            else {
                when {
                    binding.projectNameTv.text.isNullOrEmpty() -> {
                        Toast.makeText(activity, "Title cannot be left empty", Toast.LENGTH_LONG)
                    }
                    binding.projectYearTv.text.isNullOrEmpty() -> {
                        Toast.makeText(activity , "Year cannot be left empty", Toast.LENGTH_LONG)
                    }
                    else -> {
                        var description:String? = null
                        if(!binding.projectDescriptionTv.text.isNullOrEmpty()){
                            description = binding.projectDescriptionTv.text.toString()
                        }
                        if(mReseachInfoViewModel.currentResearch.value != null){
                            mReseachInfoViewModel.editResearchInfo(mReseachInfoViewModel.currentResearch.value?.id!!,
                                    binding.projectNameTv.text.toString(), description,
                                    binding.projectYearTv.text.toString().toInt(),
                                    (activity as HomeActivity).token!!
                            )
                        }

                    }
                }
            }
        }
    }
}
package com.cmpe451.platon.page.fragment.researchInfo

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.R
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentResearchInfoEditBinding
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.profilepage.ProfilePageViewModel
import com.cmpe451.platon.util.Definitions

class EditResearchInfoFragment : Fragment() {
    private lateinit var binding: FragmentResearchInfoEditBinding
    private val mReseachInfoViewModel: ResearchInfoViewModel by activityViewModels()
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()

    private lateinit var dialog:AlertDialog


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
                dialog.dismiss()
            }
        }

        mReseachInfoViewModel.getResponseDeleteResearch.observe(viewLifecycleOwner, { t->
            if (t!= null){
                when (t.first) {
                    200 -> {
                        findNavController().navigateUp()
                        Toast.makeText(activity, "Successfully deleted!", Toast.LENGTH_SHORT).show()
                    }
                    else -> Toast.makeText(activity, t.second, Toast.LENGTH_SHORT).show()
                }

            }
            dialog.dismiss()
        })

    }

    private fun setListeners() {
        dialog = Definitions().createProgressBar(activity as BaseActivity)


        binding.buttonEdit.setOnClickListener {
            // check if the title and year is empty
            if(binding.projectNameTv.text.isNullOrEmpty() && binding.projectYearTv.text.isNullOrEmpty()){
                Toast.makeText(activity as HomeActivity, "Title and Year cannot be left empty", Toast.LENGTH_LONG).show()
            }
            else {
                when {
                    binding.projectNameTv.text.isNullOrEmpty() -> {
                        Toast.makeText(activity, "Title cannot be left empty", Toast.LENGTH_LONG).show()
                    }
                    binding.projectYearTv.text.isNullOrEmpty() -> {
                        Toast.makeText(activity , "Year cannot be left empty", Toast.LENGTH_LONG).show()
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
                            dialog.show()
                        }

                    }
                }
            }
        }

        binding.buttonDelete.setOnClickListener{
            if(mReseachInfoViewModel.currentResearch.value != null){
                mReseachInfoViewModel.deleteResearchInfo(mReseachInfoViewModel.currentResearch.value?.id!!,
                        (activity as HomeActivity).token!!)
                dialog.show()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // clear search bar, and make it icon
        val search = (menu.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setQuery("", false)
        search.isIconified = true

        menu.findItem(R.id.registerFragment)?.isVisible = false
        menu.findItem(R.id.loginFragment)?.isVisible = false
        menu.findItem(R.id.search_btn)?.isVisible = false
        menu.findItem(R.id.logout_menu_btn)?.isVisible = false
        menu.findItem(R.id.notification_btn)?.isVisible = false
    }
}
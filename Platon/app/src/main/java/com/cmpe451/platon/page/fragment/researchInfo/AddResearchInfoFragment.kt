package com.cmpe451.platon.page.fragment.researchInfo

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
import com.cmpe451.platon.databinding.FragmentResearchInfoAddBinding
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.editprofile.EditProfileViewModel
import com.cmpe451.platon.page.fragment.profilepage.ProfilePageViewModel

class AddResearchInfoFragment : Fragment() {
    private lateinit var binding: FragmentResearchInfoAddBinding
    private val mReseachInfoViewModel: ResearchInfoViewModel by viewModels()
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentResearchInfoAddBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserver()
        setListeners()
    }

    private fun setObserver() {
        mReseachInfoViewModel.getResponseCode.observe(viewLifecycleOwner, { t ->
            when(t){
                "Valid Response"->{
                    Toast.makeText(activity, "Research Information is added!", Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.fetchResearch((activity as HomeActivity).token,
                        mProfilePageViewModel.getUser.value?.id
                    )
                    findNavController().navigateUp()
                }

                else ->
                    Toast.makeText(activity, t, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setListeners() {
        binding.buttonProjectAdd.setOnClickListener {
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
                        mReseachInfoViewModel.addResearchInfo(binding.projectNameTv.text.toString(), description,
                            binding.projectYearTv.text.toString().toInt(),
                            (activity as HomeActivity).token!!
                        )
                    }
                }
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
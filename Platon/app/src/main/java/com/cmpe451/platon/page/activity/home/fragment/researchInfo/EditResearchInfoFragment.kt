package com.cmpe451.platon.page.activity.home.fragment.researchInfo

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.R
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentResearchInfoEditBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.home.fragment.profilepage.ProfilePageViewModel
import com.cmpe451.platon.util.Definitions

class EditResearchInfoFragment : Fragment() {
    private lateinit var binding: FragmentResearchInfoEditBinding
    private val mResearchInfoViewModel: ResearchInfoViewModel by viewModels()
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
        mProfilePageViewModel.currentResearch.observe(viewLifecycleOwner) { t ->
            if(t != null){
                binding.projectNameTv.setText(t.title)
                binding.projectYearTv.setText(t.year.toString())
                if(t.description.isNotEmpty()){
                    binding.projectDescriptionTv.setText(t.description)
                }
                dialog.dismiss()
            }
        }

        mResearchInfoViewModel.getDeleteResearchResourceResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    Log.i("EEROROROR", "SOME ERROR OCCURRED!")
                    dialog.dismiss()
                    findNavController().navigateUp()

                }
                Resource.Error::class.java ->{
                    dialog.dismiss()
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }
            }

        })


        mResearchInfoViewModel.getEditResearchResourceResponse.observe(viewLifecycleOwner, {t ->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    findNavController().navigateUp()
                    dialog.dismiss()
                }
                Resource.Error::class.java ->{
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }


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
                        if(mProfilePageViewModel.currentResearch.value != null){
                            mResearchInfoViewModel.editResearchInfo(mProfilePageViewModel.currentResearch.value?.id!!,
                                    binding.projectNameTv.text.toString(), description,
                                    binding.projectYearTv.text.toString().toInt(),
                                    (activity as HomeActivity).currUserToken!!
                            )
                            dialog.show()
                        }

                    }
                }
            }
        }

        binding.buttonDelete.setOnClickListener{
            if(mProfilePageViewModel.currentResearch.value != null){
                mResearchInfoViewModel.deleteResearchInfo(mProfilePageViewModel.currentResearch.value?.id!!,
                        (activity as HomeActivity).currUserToken!!)
                dialog.show()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.search_btn)?.isVisible = false
        menu.findItem(R.id.notification_btn)?.isVisible = false
        menu.findItem(R.id.logout_menu_btn)?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }
}
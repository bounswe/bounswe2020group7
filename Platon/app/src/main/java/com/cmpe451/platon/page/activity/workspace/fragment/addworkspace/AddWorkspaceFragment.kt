package com.cmpe451.platon.page.activity.workspace.fragment.addworkspace

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.SkillsAdapter
import com.cmpe451.platon.databinding.AddRequirementBinding
import com.cmpe451.platon.databinding.AddSkillBinding
import com.cmpe451.platon.databinding.FragmentAddWorkspaceBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Workspace
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.page.activity.workspace.fragment.workspace.WorkspaceViewModel
import com.cmpe451.platon.util.Definitions
import java.util.*
import kotlin.collections.ArrayList

class AddWorkspaceFragment: Fragment() {

    lateinit var binding:FragmentAddWorkspaceBinding
    private val mAddWorkspaceViewModel:AddWorkspaceViewModel by viewModels()
    private val mWorkspaceViewModel:WorkspaceViewModel by activityViewModels()
    private var skillsArray:ArrayList<String> = ArrayList()
    private var requirementsArray:ArrayList<String> = ArrayList()
    private lateinit var dialog:AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentAddWorkspaceBinding.inflate(layoutInflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setListeners()
        mAddWorkspaceViewModel.getAllSkills()
        setObservers()




    }

    private fun setObservers() {
        mAddWorkspaceViewModel.getAddDeleteWorkspaceResourceResponse.observe(viewLifecycleOwner,{
            when(it.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Successfully added", Toast.LENGTH_LONG).show()
                    activity?.finish()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }

            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        dialog=Definitions().createProgressBar(requireContext())
        binding.addSkillLl.setOnClickListener {
            onAddDeleteSkillClicked()
        }
        binding.addRequirementLl.setOnClickListener {
            onAddRequirementClicked()
        }
        binding.wsDeadlineEt.setOnTouchListener { _, event ->

            if(event.action == MotionEvent.ACTION_DOWN){
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, years, months, day ->
                        val monthString = String.format("%02d", months+1)
                        val dayString = String.format("%02d", day)
                        binding.wsDeadlineEt.setText("$years.$monthString.$dayString")
                    }, year, month, dayOfMonth

                )

                datePickerDialog.show()
            }
            true

        }
        binding.buttonWorkspaceAdd.setOnClickListener {
            if(binding.wsTitleEt.text.isNullOrEmpty()){
                Toast.makeText(requireContext(), "Title cannot be left empty", Toast.LENGTH_LONG).show()
            }
            else{
                if(binding.wsDescriptionEt.text.isNullOrEmpty()){
                    Toast.makeText(requireContext(), "Description cannot be left empty", Toast.LENGTH_LONG).show()
                }
                else {
                    val title:String = binding.wsTitleEt.text.toString().trim()
                    val description:String = binding.wsDescriptionEt.text.toString().trim()
                    val isPrivate:Int = if(binding.privateSwitch.isChecked) 1 else 0
                    val max_collaborators = if(binding.wsMaxCollabNumberEt.text.isNullOrEmpty()) null else binding.wsMaxCollabNumberEt.text.toString().toInt()
                    val deadline = if(binding.wsDeadlineEt.text.isNullOrEmpty()) null else binding.wsDeadlineEt.text.toString()
                    val skillsArrayString:String? = if(skillsArray.isNotEmpty()) skillsArray.map{e-> "\"$e\""}.toString() else null
                    val requirementsArrayString:String?= if(requirementsArray.isNotEmpty()) requirementsArray.map{e-> "\"$e\""}.toString() else null
                    mAddWorkspaceViewModel.addWorkspace(title, description,isPrivate,max_collaborators, deadline, requirementsArrayString, skillsArrayString,(activity as WorkspaceActivity).token!!)
                }
            }
        }
    }

    private fun onAddRequirementClicked() {
        val tmpBinding = AddRequirementBinding.inflate(
            layoutInflater,
            requireView().parent as ViewGroup,
            false
        )
        AlertDialog.Builder(context).setView(tmpBinding.root)
            .setCancelable(false)
            .setNegativeButton("Completed") { _, _ ->
//                                    mAddWorkspaceViewModel.getAddDeleteSkillResourceResponse.removeObservers(
//                                        viewLifecycleOwner
//                                    )
            }
            .create().show()
        tmpBinding.btnAddRequirement.setOnClickListener {
            if (!tmpBinding.etNewRequirement.text.isNullOrEmpty()) {
                requirementsArray.add(tmpBinding.etNewRequirement.text.toString().trim())
                tmpBinding.etNewRequirement.text = null
                tmpBinding.etNewRequirement.hint = "Add Another Requirement"
            }
        }
    }

    private fun onAddDeleteSkillClicked() {
        mAddWorkspaceViewModel.allSkills.observe(viewLifecycleOwner,  { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
//                    val skillNameList = mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.skills
                    val bArray = t.data!!.map { skillsArray.contains(it) }.toBooleanArray()
                    AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setNeutralButton("Add Nonexistent Skill") { _, _ ->
                            val tmpBinding = AddSkillBinding.inflate(
                                layoutInflater,
                                requireView().parent as ViewGroup,
                                false
                            )
                            AlertDialog.Builder(context).setView(tmpBinding.root)
                                .setCancelable(false)
                                .setNegativeButton("Completed") { _, _ ->
//                                    mAddWorkspaceViewModel.getAddDeleteSkillResourceResponse.removeObservers(
//                                        viewLifecycleOwner
//                                    )
                                }
                                .create().show()
                            tmpBinding.btnAddSkill.setOnClickListener {
                                if (!tmpBinding.etNewSkill.text.isNullOrEmpty()) {
                                    skillsArray.add(tmpBinding.etNewSkill.text.toString().trim())
                                    tmpBinding.etNewSkill.text = null
                                    tmpBinding.etNewSkill.hint = "Add Another Skill"
                                }
                            }
                        }
                        .setNegativeButton("Completed") { _, _ ->
//                            mAddWorkspaceViewModel.getAddDeleteSkillResourceResponse.removeObservers(
//                                viewLifecycleOwner
//                            )
                        }
                        .setMultiChoiceItems(
                            t.data!!.toTypedArray(),
                            bArray
                        ) { _, which, isChecked ->
                            if (isChecked) {
                                if(t.data!![which] !in skillsArray) skillsArray.add(t.data!![which])
                            } else {
                                skillsArray.remove(t.data!![which])
                            }
                        }
                        .create().show()
                    dialog.dismiss()
                    mAddWorkspaceViewModel.allSkills.removeObservers(viewLifecycleOwner)
                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                Resource.Loading::class.java ->
                    dialog.show()
            }
        })
    }

}
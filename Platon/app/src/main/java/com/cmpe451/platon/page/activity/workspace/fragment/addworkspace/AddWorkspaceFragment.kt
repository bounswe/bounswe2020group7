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
import com.cmpe451.platon.databinding.AddSkillBinding
import com.cmpe451.platon.databinding.FragmentAddWorkspaceBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.workspace.fragment.workspace.WorkspaceViewModel
import java.util.*

class AddWorkspaceFragment: Fragment() {

    lateinit var binding:FragmentAddWorkspaceBinding
    private val mAddWorkspaceViewModel:AddWorkspaceViewModel by viewModels()
    private val mWorkspaceViewModel:WorkspaceViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentAddWorkspaceBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
//        setListeners()

        binding.wsDeadlineEt.setOnTouchListener { _, event ->

            if(event.action == MotionEvent.ACTION_DOWN){
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, years, months, day ->
                        binding.wsDeadlineEt.setText("$day/$months/$years")
                    }, year, month, dayOfMonth

                )

                datePickerDialog.show()
            }
            true

        }



    }

//    private fun setListeners() {
//        binding.addSkillIv.setOnClickListener {
//            onAddDeleteSkillClicked()
//        }
//    }
//
//    private fun onAddDeleteSkillClicked() {
//        mAddWorkspaceViewModel.allSkills.observe(viewLifecycleOwner,  { t ->
//            when (t.javaClass) {
//                Resource.Success::class.java -> {
//                    val skillNameList = mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.skills
//                    val bArray = t.data!!.map { skillNameList.contains(it) }.toBooleanArray()
//                    AlertDialog.Builder(context)
//                        .setCancelable(false)
//                        .setNeutralButton("Add Nonexistent Skill") { _, _ ->
//                            val tmpBinding = AddSkillBinding.inflate(
//                                layoutInflater,
//                                requireView().parent as ViewGroup,
//                                false
//                            )
//                            AlertDialog.Builder(context).setView(tmpBinding.root)
//                                .setCancelable(false)
//                                .setNegativeButton("Completed") { _, _ ->
//
//                                }
//                                .create().show()
//                            tmpBinding.btnAddSkill.setOnClickListener {
//                                if (!tmpBinding.etNewSkill.text.isNullOrEmpty()) {
//
//                                }
//                            }
//                        }
//                        .setNegativeButton("Completed") { _, _ ->
//
//                        }
//                        .setMultiChoiceItems(
//                            t.data!!.toTypedArray(),
//                            bArray
//                        ) { _, which, isChecked ->
//                            if (isChecked) {
//
//                            } else {
//
//                            }
//                        }
//                        .create().show()
////                    dialog.dismiss()
//                    mAddWorkspaceViewModel.allSkills.removeObservers(viewLifecycleOwner)
//                }
//                Resource.Error::class.java -> {
//                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
////                    dialog.dismiss()
//                }
//                Resource.Loading::class.java ->
////                    dialog.show()
//            }
//        })
//    }

}
package com.cmpe451.platon.page.activity.workspace.fragment.editworkspace

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.SkillsAdapter
import com.cmpe451.platon.databinding.AddSkillBinding
import com.cmpe451.platon.databinding.FragmentEditWorkspaceBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.workspace.fragment.workspace.WorkspaceViewModel

class EditWorkspaceFragment :Fragment() {

    private lateinit var binding: FragmentEditWorkspaceBinding
    private lateinit var dialog: AlertDialog
    private val mWorkspaceViewModel: WorkspaceViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentEditWorkspaceBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        initializeAdapters()
//        setObservers()
        setView()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setView() {
        binding.wsTitleEt.setText(mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.title)
        binding.wsDescriptionEt.setText(mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.description)
        binding.privateSwitch.isChecked = mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.is_private!!
        binding.wsMaxCollabNumberEt.setText(mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.max_collaborators.toString())
        val x  = ArrayAdapter(requireContext(), R.layout.spinner_item, mutableListOf("Loading..."))
        x.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spState.adapter = x
        (binding.spState.adapter as ArrayAdapter<String>).clear()
        (binding.spState.adapter as ArrayAdapter<String>).add("Search For Collaborators")
        (binding.spState.adapter as ArrayAdapter<String>).add("Ongoing")
        (binding.spState.adapter as ArrayAdapter<String>).add("Finished")
//        binding.deadlineTv.text = it.data?.deadline
//        binding.wsRequirementsEt.setText(mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.requirements)

    }


    private fun setObservers() {
        TODO("Not yet implemented")
    }

    private fun initializeAdapters() {
        TODO("Not yet implemented")
    }
}
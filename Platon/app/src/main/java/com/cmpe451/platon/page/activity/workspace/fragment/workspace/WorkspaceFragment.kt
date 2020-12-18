package com.cmpe451.platon.page.activity.workspace.fragment.workspace

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.SkillsAdapter
import com.cmpe451.platon.databinding.FragmentPersonalWorkspaceBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.util.Definitions

class WorkspaceFragment : Fragment() {

    private lateinit var binding: FragmentPersonalWorkspaceBinding
    private lateinit var dialog:AlertDialog
    private lateinit var skillsAdapter: SkillsAdapter
    private val mWorkspaceViewModel:WorkspaceViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPersonalWorkspaceBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeAdapters()
        setObservers()
        setView()
        setListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setListeners() {
        binding.buttonEditWorkspace.setOnClickListener{
            findNavController().navigate(WorkspaceFragmentDirections.actionWorkspaceFragmentToEditWorkspaceFragment())
        }
    }

    private fun setObservers() {
        dialog = Definitions().createProgressBar(requireContext())
        mWorkspaceViewModel.getWorkspaceResponse.observe(viewLifecycleOwner, {
            when(it.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    binding.workspaceTitleTv.text = it.data?.title
                    binding.descriptionTv.text = it.data?.description
                    binding.isPrivateTv.text = mWorkspaceViewModel.isPrivateString
                    binding.maxCollabTv.text = it.data?.max_collaborators.toString()
                    binding.deadlineTv.text = it.data?.deadline
                    binding.requrementsTv.text = it.data?.requirements
                    binding.stateTv.text = mWorkspaceViewModel.workspaceStateString
                    skillsAdapter.submitElements(it.data?.skills!!)
                }
            }

        })
    }
    fun setView(){
        mWorkspaceViewModel.fetchWorkspace()
    }

    private fun initializeAdapters() {
        skillsAdapter = SkillsAdapter(ArrayList(), requireContext())
        binding.rvWorkspaceSkills.adapter = skillsAdapter
        binding.rvWorkspaceSkills.layoutManager = GridLayoutManager(requireContext(), 3)
    }

}
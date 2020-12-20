package com.cmpe451.platon.page.activity.workspace.fragment.workspace

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.CollaboratorAdapter
import com.cmpe451.platon.adapter.FollowerFollowingAdapter
import com.cmpe451.platon.adapter.SkillsAdapter
import com.cmpe451.platon.databinding.FragmentPersonalWorkspaceBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Contributor
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.util.Definitions

class WorkspaceFragment : Fragment(){

    private lateinit var binding: FragmentPersonalWorkspaceBinding
    private lateinit var dialog:AlertDialog
    private lateinit var skillsAdapter: SkillsAdapter
    private lateinit var reqAdapter: SkillsAdapter
    private val mWorkspaceViewModel:WorkspaceViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentPersonalWorkspaceBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if((activity as WorkspaceActivity).addClicked != null && !(activity as WorkspaceActivity).addClicked!!){
            initializeAdapters()
            setObservers()

            setListeners()
            mWorkspaceViewModel.fetchWorkspace((activity as WorkspaceActivity).workspace_id!!, (activity as WorkspaceActivity).token!!)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setListeners() {
        binding.buttonEditWorkspace.setOnClickListener{
            findNavController().navigate(WorkspaceFragmentDirections.actionWorkspaceFragmentToEditWorkspaceFragment())
        }
        binding.descExpandLl.setOnClickListener{
            if(binding.projectInfoLl.visibility == View.GONE){
                binding.projectInfoLl.visibility = View.VISIBLE
            }
            else{
                binding.projectInfoLl.visibility = View.GONE
            }
        }
        binding.reqExpandLl.setOnClickListener{
            if(binding.rvWorkspaceRequirements.visibility == View.GONE){
                binding.rvWorkspaceRequirements.visibility = View.VISIBLE
            }
            else{
                binding.rvWorkspaceRequirements.visibility = View.GONE
            }
        }
        binding.skillsExpandLl.setOnClickListener{
            if(binding.rvWorkspaceSkills.visibility == View.GONE){
                binding.rvWorkspaceSkills.visibility = View.VISIBLE
            }
            else{
                binding.rvWorkspaceSkills.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setObservers() {
        dialog = Definitions().createProgressBar(requireContext())
        mWorkspaceViewModel.getWorkspaceResponse.observe(viewLifecycleOwner, {
            when(it.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    binding.workspaceTitleTv.text = it.data?.title
                    binding.descriptionTv.text = it.data?.description
                    binding.isPrivateTv.text = if(it.data?.is_private!!) getString(R.string.private_ws_str) else getString(R.string.public_ws_str)
                    binding.maxCollabTv.text = getString(R.string.max_collab_str) + " " + it.data?.max_collaborators.toString()
                    binding.deadlineTv.text = "Due "  + it.data?.deadline

                    skillsAdapter.submitElements(it.data?.skills ?: ArrayList())
                    reqAdapter.submitElements(it.data?.requirements ?: ArrayList())
                    binding.stateTv.text = when(it.data?.state){
                        0-> getString(R.string.state_search_for_collab_str)
                        1-> getString(R.string.state_ongoing_str)
                        else -> getString(R.string.state_finished_str)
                    }
                    (binding.collaboratorsRv.adapter as CollaboratorAdapter).submitElements(it.data?.colloborator_list ?:ArrayList())

                }
                Resource.Error::class.java ->{
                    dialog.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }

        })


    }


    private fun initializeAdapters() {
        skillsAdapter = SkillsAdapter(ArrayList(), requireContext())
        binding.rvWorkspaceSkills.adapter = skillsAdapter
        binding.rvWorkspaceSkills.layoutManager = GridLayoutManager(requireContext(), 3)

        reqAdapter = SkillsAdapter(ArrayList(), requireContext())
        binding.rvWorkspaceRequirements.adapter = reqAdapter
        binding.rvWorkspaceRequirements.layoutManager = LinearLayoutManager(requireContext())

        binding.collaboratorsRv.adapter = CollaboratorAdapter(ArrayList(), requireContext())
        binding.collaboratorsRv.layoutManager = LinearLayoutManager(requireContext())

    }



}
package com.cmpe451.platon.page.activity.workspace.fragment.workspace

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
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
import com.bumptech.glide.Glide
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.CollaboratorAdapter
import com.cmpe451.platon.adapter.FollowerFollowingAdapter
import com.cmpe451.platon.adapter.SkillsAdapter
import com.cmpe451.platon.databinding.AddRequirementBinding
import com.cmpe451.platon.databinding.AddSkillBinding
import com.cmpe451.platon.databinding.FragmentPersonalWorkspaceBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Contributor
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.page.activity.workspace.fragment.addworkspace.AddWorkspaceViewModel
import com.cmpe451.platon.util.Definitions

class WorkspaceFragment : Fragment(){

    private lateinit var binding: FragmentPersonalWorkspaceBinding
    private lateinit var dialog:AlertDialog
    private lateinit var skillsAdapter: SkillsAdapter
    private lateinit var reqAdapter: SkillsAdapter
    private val mWorkspaceViewModel:WorkspaceViewModel by activityViewModels()
    private val mAddWorkspaceViewModel:AddWorkspaceViewModel by viewModels()
    private var skillsArray:ArrayList<String> = ArrayList()
    private var requirementsArray:ArrayList<String> = ArrayList()
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
        binding.infoTitle.setOnClickListener{
            findNavController().navigate(WorkspaceFragmentDirections.actionWorkspaceFragmentToEditWorkspaceFragment())
        }
        binding.descTitleTv.setOnClickListener{
            if(binding.projectInfoLl.visibility == View.GONE){
                binding.projectInfoLl.visibility = View.VISIBLE
            }
            else{
                binding.projectInfoLl.visibility = View.GONE
            }
        }
        binding.requirementsTitleTv.setOnClickListener{
            if(binding.rvWorkspaceRequirements.visibility == View.GONE){
                binding.rvWorkspaceRequirements.visibility = View.VISIBLE
                binding.addRequirementIv.visibility = View.VISIBLE
            }
            else{
                binding.rvWorkspaceRequirements.visibility = View.GONE
                binding.addRequirementIv.visibility = View.GONE
            }
        }
        binding.skillsTitleTv.setOnClickListener{
            if(binding.rvWorkspaceSkills.visibility == View.GONE){
                binding.rvWorkspaceSkills.visibility = View.VISIBLE
                binding.addSkillIv.visibility = View.VISIBLE
            }
            else{
                binding.rvWorkspaceSkills.visibility = View.GONE
                binding.addSkillIv.visibility = View.GONE
            }
        }
        binding.addSkillIv.setOnClickListener {
            onAddDeleteSkillClicked()
        }
        binding.addRequirementIv.setOnClickListener {
            onAddRequirementClicked()
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
                    if(it.data?.max_collaborators == null)
                        binding.maxCollabTv.visibility = View.GONE
                    else
                        binding.maxCollabTv.text = getString(R.string.max_collab_str) + " " + it.data?.max_collaborators.toString()
                    if(it.data?.deadline==null)
                        binding.deadlineTv.visibility = View.GONE
                    else
                        binding.deadlineTv.text = "Due "  + it.data?.deadline
                    skillsAdapter.submitElements(it.data?.skills ?: ArrayList())
                    reqAdapter.submitElements(it.data?.requirements ?: ArrayList())
                    binding.stateTv.text = when(it.data?.state){
                        0-> getString(R.string.state_search_for_collab_str)
                        1-> getString(R.string.state_ongoing_str)
                        else -> getString(R.string.state_finished_str)
                    }
                    (binding.collaboratorsRv.adapter as CollaboratorAdapter).submitElements(it.data?.active_contributors ?:ArrayList())

                }
                Resource.Error::class.java ->{
                    dialog.dismiss()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
            }

        })
        mWorkspaceViewModel.getUpdateResourceResponse.observe(viewLifecycleOwner,{
            when(it.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Successfully updated", Toast.LENGTH_LONG).show()
                    mWorkspaceViewModel.fetchWorkspace((activity as WorkspaceActivity).workspace_id!!, (activity as WorkspaceActivity).token!!)
                    mWorkspaceViewModel.getUpdateResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
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
    private fun onAddRequirementClicked() {
        requirementsArray.clear()

        val requirementsList = mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.requirements
        requirementsArray.addAll(requirementsList)
        val bArray = mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.requirements.map { requirementsList.contains(it) }.toBooleanArray()
        AlertDialog.Builder(context)
            .setCancelable(false)
            .setNeutralButton("Add Nonexistent Requirement") { _, _ ->
                val tmpBinding = AddRequirementBinding.inflate(
                    layoutInflater,
                    requireView().parent as ViewGroup,
                    false
                )
                val addNewReq = AlertDialog.Builder(context).setView(tmpBinding.root)
                    .setCancelable(false)
                    .setNegativeButton("Completed",DialogInterface.OnClickListener { dialog, _ ->
                        val requirementsArrayString:String?= if(requirementsArray.isNotEmpty()) requirementsArray.map{e-> "\"$e\""}.toString() else "[]"
                        mWorkspaceViewModel.updateWorkspace((activity as WorkspaceActivity).workspace_id!!,null, null,
                            null, null, null, requirementsArrayString,null,null, (activity as WorkspaceActivity).token!!)
                    })
                    .create().show()
                tmpBinding.btnAddRequirement.setOnClickListener {
                    if (!tmpBinding.etNewRequirement.text.isNullOrEmpty()) {
                        requirementsArray.add(tmpBinding.etNewRequirement.text.toString().trim())
                        tmpBinding.etNewRequirement.text = null
                        tmpBinding.etNewRequirement.hint = "Add Another Requirement"
                    }
                }
            }
            .setNegativeButton("Completed") { _, _ ->
                val requirementsArrayString:String?= if(requirementsArray.isNotEmpty()) requirementsArray.map{e-> "\"$e\""}.toString() else "[]"
                mWorkspaceViewModel.updateWorkspace((activity as WorkspaceActivity).workspace_id!!,null, null,
                    null, null, null, requirementsArrayString,null,null, (activity as WorkspaceActivity).token!!)

            }
            .setMultiChoiceItems(
                requirementsList.toTypedArray(),
                bArray
            ) { _, which, isChecked ->
                if (isChecked) {
                    if(requirementsList[which] !in skillsArray) skillsArray.add(requirementsList[which])
                } else {
                    requirementsArray.remove(requirementsList[which])
                }
            }
            .create().show()
    }

    private fun onAddDeleteSkillClicked() {
        mAddWorkspaceViewModel.getAllSkills()
        mAddWorkspaceViewModel.allSkills.observe(viewLifecycleOwner,  { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    val skillNameList = mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.skills
                    skillsArray.clear()
                    skillsArray.addAll(skillNameList)
                    val bArray = t.data!!.map { skillNameList.contains(it) }.toBooleanArray()
                    AlertDialog.Builder(context)
                        .setNeutralButton("Add Nonexistent Skill") { _, _ ->
                            val tmpBinding = AddSkillBinding.inflate(
                                layoutInflater,
                                requireView().parent as ViewGroup,
                                false
                            )
                            AlertDialog.Builder(context).setView(tmpBinding.root)
                                .setNegativeButton("Completed") { _, _ ->
                                    val skillsArrayString: String = if(skillsArray.isNotEmpty()) skillsArray.map{ e-> "\"$e\""}.toString() else "[]"
                                    mWorkspaceViewModel.updateWorkspace((activity as WorkspaceActivity).workspace_id!!,null, null,
                                        null, null, null, null,skillsArrayString,null, (activity as WorkspaceActivity).token!!)
                                    mAddWorkspaceViewModel.allSkills.removeObservers(viewLifecycleOwner)
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
                            val skillsArrayString: String = if(skillsArray.isNotEmpty()) skillsArray.map{ e-> "\"$e\""}.toString() else "[]"
                            mWorkspaceViewModel.updateWorkspace((activity as WorkspaceActivity).workspace_id!!,null, null,
                                null, null, null, null,skillsArrayString,null, (activity as WorkspaceActivity).token!!)
                            mAddWorkspaceViewModel.allSkills.removeObservers(viewLifecycleOwner)
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
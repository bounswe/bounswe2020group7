package com.cmpe451.platon.page.activity.workspace.fragment.workspace

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
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
import com.cmpe451.platon.adapter.MilestoneAdapter
import com.cmpe451.platon.adapter.SkillsAdapter
import com.cmpe451.platon.databinding.*
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Contributor
import com.cmpe451.platon.network.models.Milestone
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.page.activity.workspace.fragment.addworkspace.AddWorkspaceViewModel
import com.cmpe451.platon.util.Definitions
import java.util.*
import kotlin.collections.ArrayList

class WorkspaceFragment : Fragment(), MilestoneAdapter.MilestoneButtonClickListener{

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
            initViews()

        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initViews() {
        if(!(activity as WorkspaceActivity).isOwner!!){
            binding.addRequirementIv.visibility = View.GONE
            binding.addSkillIv.visibility = View.GONE
            binding.infoTitle.setCompoundDrawables(null,null,null,null)
            binding.milestoneTitleTv.setCompoundDrawables(null,null,null,null)
        }
        binding.collabTitleTv.setCompoundDrawables(null,null,null,null)
        mWorkspaceViewModel.fetchWorkspace((activity as WorkspaceActivity).workspace_id!!, (activity as WorkspaceActivity).token!!)
        getMilestones()
    }

    private fun getMilestones() {
        mWorkspaceViewModel.getMilestones((activity as WorkspaceActivity).workspace_id!!, null, null, (activity as WorkspaceActivity).token!!)
    }

    private fun setListeners() {
        if((activity as WorkspaceActivity).isOwner!!){
            binding.infoTitle.setOnClickListener{
//            findNavController().navigate(WorkspaceFragmentDirections.actionWorkspaceFragmentToEditWorkspaceFragment())
                onUpdateButtonClicked()
            }
            binding.addSkillIv.setOnClickListener {
                onAddDeleteSkillClicked()
            }
            binding.addRequirementIv.setOnClickListener {
                onAddRequirementClicked()
            }
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
            if(binding.requirementLl.visibility == View.GONE){
                binding.requirementLl.visibility = View.VISIBLE
            }
            else{
                binding.requirementLl.visibility = View.GONE
            }
        }
        binding.skillsTitleTv.setOnClickListener{
            if(binding.skillLl.visibility == View.GONE){
                binding.skillLl.visibility = View.VISIBLE
            }
            else{
                binding.skillLl.visibility = View.GONE
            }
        }
        binding.milestoneTitleTv.setOnClickListener {
            onAddMilestoneClicked()
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
                        binding.maxCollabTv.text = getString(R.string.max_collab_str) + ": " + it.data?.max_collaborators.toString()
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
                    Toast.makeText(requireContext(), "Successfully updated", Toast.LENGTH_LONG).show()
                    mWorkspaceViewModel.fetchWorkspace((activity as WorkspaceActivity).workspace_id!!, (activity as WorkspaceActivity).token!!)
                    mWorkspaceViewModel.getUpdateResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    mWorkspaceViewModel.getUpdateResourceResponse.value = Resource.Done()

                }
                Resource.Done::class.java ->dialog.dismiss()
            }
        })
        mWorkspaceViewModel.getDeleteResourceResponse.observe(viewLifecycleOwner, {
            when (it.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    Toast.makeText(requireContext(), "Successfully deleted", Toast.LENGTH_LONG)
                        .show()
                    activity?.finish()
                    mWorkspaceViewModel.getUpdateResourceResponse.value = Resource.Done()

                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    mWorkspaceViewModel.getUpdateResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java ->{
                    dialog.dismiss()
                }
            }
        })
        mWorkspaceViewModel.getMilestoneResponse.observe(viewLifecycleOwner, {
            when (it.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    (binding.milestoneRv.adapter as MilestoneAdapter).replaceElements(it.data!!.result)
                    mWorkspaceViewModel.getMilestoneResponse.value = Resource.Done()

                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    mWorkspaceViewModel.getMilestoneResponse.value = Resource.Done()
                }
                Resource.Done::class.java ->{
                    dialog.dismiss()
                }
            }
        })
        mWorkspaceViewModel.getAddDeleteUpdateMilestoneResponse.observe(viewLifecycleOwner, {
            when (it.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    getMilestones()
                    mWorkspaceViewModel.getMilestoneResponse.value = Resource.Done()

                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    mWorkspaceViewModel.getMilestoneResponse.value = Resource.Done()
                }
                Resource.Done::class.java ->{
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
        binding.collaboratorsRv.layoutManager = GridLayoutManager(requireContext(),2)

        binding.milestoneRv.adapter = MilestoneAdapter(ArrayList(),this, (activity as WorkspaceActivity).isOwner!!)
        binding.milestoneRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    }
    private fun onAddRequirementClicked() {
        requirementsArray.clear()

        val requirementsList = mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.requirements
        requirementsArray.addAll(requirementsList)
        val bArray = mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.requirements.map { requirementsList.contains(it) }.toBooleanArray()
        AlertDialog.Builder(context)
            .setCancelable(false)
            .setNeutralButton("Add Nonexistent") { _, _ ->
                val tmpBinding = AddRequirementBinding.inflate(
                    layoutInflater,
                    requireView().parent as ViewGroup,
                    false
                )
                AlertDialog.Builder(context).setView(tmpBinding.root)
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
    @SuppressLint("ClickableViewAccessibility")
    private fun onUpdateButtonClicked(){
        val tmpBinding = FragmentEditWorkspaceBinding.inflate(
            layoutInflater,
            requireView().parent as ViewGroup,
            false
        )
        val editDialog = AlertDialog.Builder(context).setView(tmpBinding.root)
            .setCancelable(true)
            .show()
        tmpBinding.wsTitleEt.setText(mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.title)
        tmpBinding.wsDescriptionEt.setText(mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.description)
        tmpBinding.privateSwitch.isChecked =
            mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.is_private
        tmpBinding.wsMaxCollabNumberEt.setText(mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.max_collaborators.toString())
        val x = ArrayAdapter(requireContext(), R.layout.spinner_item, mutableListOf("Loading..."))
        x.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tmpBinding.spState.adapter = x
        (tmpBinding.spState.adapter as ArrayAdapter<String>).clear()
        (tmpBinding.spState.adapter as ArrayAdapter<String>).add(getString(R.string.state_search_for_collab_str))
        (tmpBinding.spState.adapter as ArrayAdapter<String>).add(getString(R.string.state_ongoing_str))
        (tmpBinding.spState.adapter as ArrayAdapter<String>).add(getString(R.string.state_finished_str))
        tmpBinding.spState.setSelection(mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.state)
        tmpBinding.wsDeadlineEt.setOnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_DOWN) {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, years, months, day ->
                        val monthString = String.format("%02d", months+1)
                        val dayString = String.format("%02d", day)
                        tmpBinding.wsDeadlineEt.setText("$years.$monthString.$dayString")
                    }, year, month, dayOfMonth

                )

                datePickerDialog.show()
            }
            true


        }
        tmpBinding.deleteWsBtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage("Are you sure you want to delete " + mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.title + "?")
                .setPositiveButton("Delete"
                ) { _, _ ->
                    mWorkspaceViewModel.deleteWorkspace(
                        (activity as WorkspaceActivity).workspace_id!!,
                        (activity as WorkspaceActivity).token!!
                    )
                }
                .setNegativeButton("Cancel"
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        }
        tmpBinding.updateWsBtn.setOnClickListener {
            if (tmpBinding.wsTitleEt.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Title cannot be left empty", Toast.LENGTH_LONG)
                    .show()
            } else {
                if (tmpBinding.wsDescriptionEt.text.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Description cannot be left empty",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val title: String = tmpBinding.wsTitleEt.text.toString().trim()
                    val description: String = tmpBinding.wsDescriptionEt.text.toString().trim()
                    val isPrivate: Int = if (tmpBinding.privateSwitch.isChecked) 1 else 0
                    val max_collaborators =
                        if (tmpBinding.wsMaxCollabNumberEt.text.isNullOrEmpty()) null else tmpBinding.wsMaxCollabNumberEt.text.toString()
                            .toInt()
                    val deadline =
                        if (tmpBinding.wsDeadlineEt.text.isNullOrEmpty()) null else tmpBinding.wsDeadlineEt.text.toString()
                    val state = tmpBinding.spState.selectedItemPosition
                    mWorkspaceViewModel.updateWorkspace(
                        (activity as WorkspaceActivity).workspace_id!!,
                        title,
                        description,
                        isPrivate,
                        max_collaborators,
                        deadline,
                        null,
                        null,
                        state,
                        (activity as WorkspaceActivity).token!!

                    )

                }
            }
            editDialog.dismiss()
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onAddMilestoneClicked(){
        val tmpBinding = DialogAddMilestoneBinding.inflate(
            layoutInflater,
            requireView().parent as ViewGroup,
            false
        )
        val addDialog = AlertDialog.Builder(context).setView(tmpBinding.root)
            .setCancelable(true)
            .show()
        tmpBinding.milestoneDeadlineEt.setOnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_DOWN) {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, years, months, day ->
                        val monthString = String.format("%02d", months+1)
                        val dayString = String.format("%02d", day)
                        tmpBinding.milestoneDeadlineEt.setText("$years-$monthString-$dayString")
                    }, year, month, dayOfMonth

                )

                datePickerDialog.show()
            }
            true


        }

        tmpBinding.milestoneDateEt.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val datePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hours, minutes ->
                        val h = String.format("%02d", hours)
                        val m = String.format("%02d", minutes)
                        tmpBinding.milestoneDateEt.setText("$h:$m:00")
                    }, hour, minute, true

                )

                datePickerDialog.show()
            }
            true

        }
        tmpBinding.buttonMilestoneAdd.setOnClickListener {
            if(tmpBinding.milestoneTitleEt.text.isNullOrEmpty()){
                Toast.makeText(requireContext(), "Title cannot be left empty", Toast.LENGTH_LONG).show()
            }
            else {
                if(tmpBinding.milestoneDescriptionEt.text.isNullOrEmpty()){
                    Toast.makeText(requireContext(), "Description cannot be left empty", Toast.LENGTH_LONG).show()
                }
                else {
                    if(tmpBinding.milestoneDeadlineEt.text.isNullOrEmpty()){
                        Toast.makeText(requireContext(), "Date cannot be left empty", Toast.LENGTH_LONG).show()
                    }
                    else {
                        if(tmpBinding.milestoneDateEt.text.isNullOrEmpty()){
                            Toast.makeText(requireContext(), "Time cannot be left empty", Toast.LENGTH_LONG).show()
                        }
                        else {
                            val title = tmpBinding.milestoneTitleEt.text.toString().trim()
                            val desc = tmpBinding.milestoneDescriptionEt.text.toString().trim()
                            val date = tmpBinding.milestoneDeadlineEt.text.toString().trim()
                            val time = tmpBinding.milestoneDateEt.text.toString().trim()
                            val deadline = "$date $time"
                            val token = (activity as WorkspaceActivity).token!!
                            mWorkspaceViewModel.addMilestone((activity as WorkspaceActivity).workspace_id!!,
                                title,desc, deadline, token)
                            addDialog.dismiss()
                        }
                    }
                }
            }
        }


    }


    @SuppressLint("ClickableViewAccessibility")
    private fun onUpdateMilestoneClicked(milestone:Milestone){
        val tmpBinding = DialogAddMilestoneBinding.inflate(
            layoutInflater,
            requireView().parent as ViewGroup,
            false
        )
        val editMSDialog = AlertDialog.Builder(context).setView(tmpBinding.root)
            .setCancelable(true)
            .show()

        tmpBinding.milestoneTitleEt.setText(milestone.title)
        tmpBinding.milestoneDescriptionEt.setText(milestone.description)
        tmpBinding.buttonMilestoneAdd.text = getString(R.string.update_str)
        tmpBinding.milestoneDeadlineEt.setOnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_DOWN) {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, years, months, day ->
                        val monthString = String.format("%02d", months+1)
                        val dayString = String.format("%02d", day)
                        tmpBinding.milestoneDeadlineEt.setText("$years-$monthString-$dayString")
                    }, year, month, dayOfMonth

                )

                datePickerDialog.show()
            }
            true


        }

        tmpBinding.milestoneDateEt.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val datePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hours, minutes ->
                        val h = String.format("%02d", hours)
                        val m = String.format("%02d", minutes)
                        tmpBinding.milestoneDateEt.setText("$h:$m:00")
                    }, hour, minute, true

                )

                datePickerDialog.show()
            }
            true

        }
        tmpBinding.buttonMilestoneAdd.setOnClickListener {


            if(tmpBinding.milestoneTitleEt.text.isNullOrEmpty()){
                Toast.makeText(requireContext(), "Title cannot be left empty", Toast.LENGTH_LONG).show()
            }
            else {
                if(tmpBinding.milestoneDescriptionEt.text.isNullOrEmpty()){
                    Toast.makeText(requireContext(), "Description cannot be left empty", Toast.LENGTH_LONG).show()
                }
                else {
                    if(tmpBinding.milestoneDeadlineEt.text.isNullOrEmpty() && tmpBinding.milestoneDateEt.text.isNullOrEmpty()){

                        val title = tmpBinding.milestoneTitleEt.text.toString().trim()
                        val desc = tmpBinding.milestoneDescriptionEt.text.toString().trim()
                        val deadline = null
                        val token = (activity as WorkspaceActivity).token!!
                        mWorkspaceViewModel.updateMilestone((activity as WorkspaceActivity).workspace_id!!,milestone.milestone_id,
                            title,desc, deadline, token)
                        editMSDialog.dismiss()

                    }
                    else {
                        if(tmpBinding.milestoneDeadlineEt.text.isNullOrEmpty()){
                            Toast.makeText(requireContext(), "Date cannot be left empty", Toast.LENGTH_LONG).show()
                        }
                        else{
                            if(tmpBinding.milestoneDateEt.text.isNullOrEmpty()){
                                Toast.makeText(requireContext(), "Time cannot be left empty", Toast.LENGTH_LONG).show()
                            }
                            else {
                                val title = tmpBinding.milestoneTitleEt.text.toString().trim()
                                val desc = tmpBinding.milestoneDescriptionEt.text.toString().trim()
                                val date = tmpBinding.milestoneDeadlineEt.text.toString().trim()
                                val time = tmpBinding.milestoneDateEt.text.toString().trim()
                                val deadline = "$date $time"
                                val token = (activity as WorkspaceActivity).token!!
                                mWorkspaceViewModel.updateMilestone((activity as WorkspaceActivity).workspace_id!!,milestone.milestone_id,
                                    title,desc, deadline, token)
                                editMSDialog.dismiss()
                            }
                        }
                    }

                }
            }
        }


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

    override fun onMilestoneNameClicked(binding: MilestoneCellBinding) {
        if(binding.expandLl.visibility == View.GONE) binding.expandLl.visibility = View.VISIBLE
        else binding.expandLl.visibility = View.GONE

    }

    override fun onEditMilestoneClicked(milestone: Milestone) {
        onUpdateMilestoneClicked(milestone)
    }

    override fun onDeleteMilestoneClicked(milestone: Milestone) {
        AlertDialog.Builder(context)
            .setMessage("Are you sure you want to delete ${milestone.title}? You will not be able to undo.")
            .setPositiveButton("Delete"
            ) { _, _ ->
                mWorkspaceViewModel.deleteMilestone((activity as WorkspaceActivity).workspace_id!!,
                milestone.milestone_id, (activity as WorkspaceActivity).token!!)
            }
            .setNegativeButton("Cancel"
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }


}
package com.cmpe451.platon.page.activity.workspace.fragment.issuedetail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.AssigneeAdapter
import com.cmpe451.platon.adapter.IssueCommentAdapter
import com.cmpe451.platon.databinding.*
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.*
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.util.Definitions
import java.util.*
import kotlin.collections.ArrayList

class IssueDetailFragment: Fragment() {
    private lateinit var dialog: AlertDialog
    private val mIssueDetailViewModel: IssueDetailViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var paginationListenerComments: PaginationListener

    lateinit var issue_id: String
    lateinit var issue_title: String
    lateinit var issue_description: String
    lateinit var issue_creator_name: String
    lateinit var issue_deadline: String
    lateinit var contributors:List<Contributor>
    lateinit var assignees:List<Assignee>
    private var maxPageNumberComment:Int=0
    private var pageSize:Int=10




    lateinit var binding: FragmentIssueDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentIssueDetailBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        sharedPreferences = activity?.getSharedPreferences("token_file", Context.MODE_PRIVATE)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateIssueInformations()
        initViews()
        setListeners()
        setObservers()
        mIssueDetailViewModel.getIssueAssignee((activity as WorkspaceActivity).workspace_id!!,issue_id.toInt(), null, null, (activity as WorkspaceActivity).token!!)
        mIssueDetailViewModel.fetchWorkspace((activity as WorkspaceActivity).workspace_id!!, (activity as WorkspaceActivity).token!!)
        mIssueDetailViewModel.getIssueComments((activity as WorkspaceActivity).workspace_id!!, issue_id.toInt(), 0, pageSize,(activity as WorkspaceActivity).token!!)

    }


    private fun updateIssueInformations() {
        issue_id = sharedPreferences.getString("issue_id", null).toString()
        issue_title = sharedPreferences.getString("issue_title", null).toString()
        issue_description = sharedPreferences.getString("issue_description", null).toString()
        issue_creator_name = sharedPreferences.getString("issue_creator_name", null).toString()
        issue_deadline = sharedPreferences.getString("issue_deadline", null).toString()
    }


    private fun setObservers(){


        mIssueDetailViewModel.assigneeResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    assignees = t.data!!.result
                    (binding.issueAssignee.adapter as AssigneeAdapter).submitElements(t.data!!.result)

                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Loading::class.java -> {
                    //Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
            }
        })

        mIssueDetailViewModel.deleteIssueResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    findNavController().navigateUp()
                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Loading::class.java -> {
                    //Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
            }
        })

        mIssueDetailViewModel.editIssueResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java ->{

                    Toast.makeText(requireContext(), "Succesfully updated.", Toast.LENGTH_SHORT).show()

                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), "hatalı request", Toast.LENGTH_SHORT).show()
                    activity?.supportFragmentManager?.popBackStack()
                }
                Resource.Loading::class.java -> {
                    //Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
            }
        })

        mIssueDetailViewModel.getWorkspaceResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    contributors = t.data!!.active_contributors
                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    activity?.supportFragmentManager?.popBackStack()
                }
                Resource.Loading::class.java -> {
                    //Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
            }
        })

        mIssueDetailViewModel.addIssueAssigneeResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    mIssueDetailViewModel.getIssueAssignee((activity as WorkspaceActivity).workspace_id!!,issue_id.toInt(), null, null, (activity as WorkspaceActivity).token!!)
                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Loading::class.java -> {
                    //Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
            }
        })

        mIssueDetailViewModel.deleteIssueAssigneeResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    mIssueDetailViewModel.getIssueAssignee((activity as WorkspaceActivity).workspace_id!!,issue_id.toInt(), null, null, (activity as WorkspaceActivity).token!!)

                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Loading::class.java -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
            }
        })

        mIssueDetailViewModel.getIssueCommentsResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Loading::class.java->{
                    dialog.show()
                }
                Resource.Success::class.java ->{
                    //(binding.issuesRecyclerView.adapter as IssuesAdapter).clearElements()
                    val issue = t.data!!.result as ArrayList<IssueComment>
                    maxPageNumberComment = t.data!!.number_of_pages
                    (binding.issueCommentsRecyclerView.adapter as IssueCommentAdapter).submitElements(issue)
                    mIssueDetailViewModel.getIssueCommentsResponse.value = Resource.Done()
                    paginationListenerComments.isLoading = false
                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    mIssueDetailViewModel.getIssueCommentsResponse.value = Resource.Done()
                }
                Resource.Done::class.java->{
                    dialog.dismiss()
                }
            }
        })

        mIssueDetailViewModel.addIssueCommentResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Loading::class.java->{
                    dialog.show()
                }
                Resource.Success::class.java ->{
                    (binding.issueCommentsRecyclerView.adapter as IssueCommentAdapter).clearElements()
                    mIssueDetailViewModel.getIssueComments((activity as WorkspaceActivity).workspace_id!!, issue_id.toInt(), maxPageNumberComment, pageSize,(activity as WorkspaceActivity).token!!)
                    mIssueDetailViewModel.addIssueCommentResponse.value = Resource.Done()
                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    mIssueDetailViewModel.addIssueCommentResponse.value = Resource.Done()
                }
                Resource.Done::class.java->{
                    dialog.dismiss()
                }
            }
        })










    }
    private fun initViews() {

        val layoutManagerComments = LinearLayoutManager(this.activity)
        paginationListenerComments = object:PaginationListener(layoutManagerComments, pageSize) {
            override fun loadMoreItems() {
                if (maxPageNumberComment - 1 > currentPage) {
                    isLoading = true
                    currentPage++
                    //TODO: get implement
                    //mIssueDetailViewModel.getComments()
                }

            }

            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        }

        binding.issueCommentsRecyclerView.layoutManager = layoutManagerComments
        binding.issueCommentsRecyclerView.adapter = IssueCommentAdapter(ArrayList(),requireContext(), this, (activity as WorkspaceActivity).user_id!!)
        binding.issueCommentsRecyclerView.addOnScrollListener(paginationListenerComments)

        val height = resources.displayMetrics.heightPixels
        val width = resources.displayMetrics.widthPixels
        binding.issueTitle.text = issue_title
        binding.issueDescriptionTextView.text = issue_description
        binding.issueCreatorName.text = issue_creator_name
        binding.issueDeadline.text = issue_deadline

        binding.issueAssignee.adapter = AssigneeAdapter(ArrayList(), requireContext())
        binding.issueAssignee.layoutManager = GridLayoutManager(requireContext(), 2)

    }


    private fun setListeners() {
        dialog = Definitions().createProgressBar(requireContext())

        binding.issueDescription.setOnClickListener{
            if(binding.issueInfoLinearLayout.visibility == View.GONE){
                binding.issueInfoLinearLayout.visibility = View.VISIBLE
            }
            else{
                binding.issueInfoLinearLayout.visibility = View.GONE
            }
        }

        binding.infoTitle.setOnClickListener{
            onUpdateButtonClicked()
        }

        binding.issueCommentsTitle.setOnClickListener{
            when(binding.issueCommentsRecyclerView.visibility){
                View.GONE->{
                    var collaboratorIds = ArrayList<Int>()

                    for (item in contributors) {
                        collaboratorIds.add(item.id)
                    }

                    if (collaboratorIds.contains((activity as WorkspaceActivity).user_id!!)) {
                        binding.issueAddComment.visibility = View.VISIBLE
                    }
                    binding.issueCommentsRecyclerView.visibility = View.VISIBLE
                }
                View.VISIBLE->{
                    var collaboratorIds = ArrayList<Int>()

                    for (item in contributors) {
                        collaboratorIds.add(item.id)
                    }

                    if (collaboratorIds.contains((activity as WorkspaceActivity).user_id!!)) {
                        binding.issueAddComment.visibility = View.GONE
                    }
                    binding.issueCommentsRecyclerView.visibility = View.GONE
                }
            }

        }

        binding.issueAssigneeTextView.setOnClickListener{
            onAddAssigneeClicked()
        }

        binding.issueAddComment.setOnClickListener{
            onAddCommentClicked()
        }
    }



    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.issue_btn)?.isVisible = false
        menu.findItem(R.id.add_issue_btn)?.isVisible = false
        menu.findItem(R.id.btn_WorkspaceApplications)?.isVisible = false
        menu.findItem(R.id.workspaceFolderFragment)?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }



    @SuppressLint("ClickableViewAccessibility")
    private fun onUpdateButtonClicked() {
        val tmpBinding = FragmentEditIssueBinding.inflate(
            layoutInflater,
            requireView().parent as ViewGroup,
            false
        )

        tmpBinding.issueDescriptionEt.setText(binding.issueDescriptionTextView.text)
        tmpBinding.issueTitleEt.setText(binding.issueTitle.text)

        tmpBinding.issueDeadline.setOnTouchListener { _, event ->

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
                        tmpBinding.issueDeadline.setText("$years-$monthString-$dayString")
                    }, year, month, dayOfMonth

                )

                datePickerDialog.show()
            }
            true


        }

        tmpBinding.issueTimeEt.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val datePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hours, minutes ->
                        val h = String.format("%02d", hours)
                        val m = String.format("%02d", minutes)
                        tmpBinding.issueTimeEt.setText("$h:$m:00")
                    }, hour, minute, true

                )

                datePickerDialog.show()
            }
            true

        }


        val editDialog = AlertDialog.Builder(context).setView(tmpBinding.root)
            .setCancelable(true)
            .show()
        editDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tmpBinding.deleteIssueBtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage("Are you sure you want to delete? ")
                .setPositiveButton(
                    "Delete"
                ) { _, _ ->
                    mIssueDetailViewModel.deleteIssue((activity as WorkspaceActivity).workspace_id!!, issue_id.toInt(), (activity as WorkspaceActivity).token!!)
                    editDialog.dismiss()
                }
                .setNegativeButton(
                    "Cancel"
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        }
        tmpBinding.updateIssueBtn.setOnClickListener {
            if (tmpBinding.issueTitleEt.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Title cannot be left empty", Toast.LENGTH_LONG)
                    .show()
            } else {
                if (tmpBinding.issueDescriptionEt.text.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Description cannot be left empty",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val title: String = tmpBinding.issueTitleEt.text.toString().trim()
                    val description: String = tmpBinding.issueDescriptionEt.text.toString().trim()
                    if(tmpBinding.issueDeadline.text.isNullOrEmpty() && !tmpBinding.issueTimeEt.text.isNullOrEmpty()) {
                        Toast.makeText(requireContext(), "Deadline cannot be empty.", Toast.LENGTH_SHORT).show()

                    } else if(!tmpBinding.issueDeadline.text.isNullOrEmpty() && tmpBinding.issueTimeEt.text.isNullOrEmpty()) {
                        Toast.makeText(requireContext(), "Time cannot be empty.", Toast.LENGTH_SHORT).show()

                    } else {
                        val deadline =
                            if (tmpBinding.issueDeadline.text.isNullOrEmpty() || tmpBinding.issueTitleEt.text.isNullOrEmpty()) null else tmpBinding.issueDeadline.text.toString() + " " + tmpBinding.issueTimeEt.text.toString()
                        mIssueDetailViewModel.editIssue((activity as WorkspaceActivity).workspace_id!!, issue_id.toInt(), title, description, deadline, (activity as WorkspaceActivity).token!!)
                        editDialog.dismiss()
                        binding.issueDescriptionTextView.text = description
                        if(deadline != null) {
                            binding.issueDeadline.text = deadline
                        }
                        binding.issueTitle.text = title
                        tmpBinding.issueDescriptionEt.setText(description)
                        tmpBinding.issueTitleEt.setText(title)

                    }

                }
            }

        }
    }

    private fun onAddAssigneeClicked() {

        var contributorNames = ArrayList<String>()
        var contributorIds = ArrayList<Int>()
        var selectedId = ArrayList<Int>()
        var unselectedId = ArrayList<Int>()


        for(item in contributors) {
            var temp = item.name + " " + item.surname
            contributorNames.add(temp)
            contributorIds.add(item.id)
        }

        var assigneeNames = ArrayList<String>()
        var assigneeIds = ArrayList<Int>()
        for(item in assignees) {
            assigneeNames.add(item.assignee_name + " " + item.assignee_surname)
            assigneeIds.add(item.assignee_id)
        }

        val bArray = contributorIds.map { assigneeIds.contains(it) }.toBooleanArray()
        AlertDialog.Builder(context)
            .setNegativeButton("Add Assignee") { _, _ -> print("ert")
                for (item in selectedId) {
                    mIssueDetailViewModel.addIssueAssignee((activity as WorkspaceActivity).workspace_id!!, issue_id.toInt(), item, (activity as WorkspaceActivity).token!!)
                }

                for(item in unselectedId) {
                    mIssueDetailViewModel.deleteIssueAssignee((activity as WorkspaceActivity).workspace_id!!, issue_id.toInt(), item, (activity as WorkspaceActivity).token!!)
                }

            }
            .setMultiChoiceItems(
                contributorNames.toTypedArray(),
                bArray
            ) { _, which, isChecked ->

                if (isChecked) {
                    selectedId.add(contributorIds[which])
                } else {
                    unselectedId.add(contributorIds[which])
                }

            }
            .create().show()
        dialog.dismiss()

    }

    private fun onAddCommentClicked() {
        val addBinding = DialogIssueAddCommentBinding.inflate(layoutInflater, binding.root, false)
        val addCommentDialog = AlertDialog.Builder(requireContext())
            .setView(addBinding.root)
            .show()
        addCommentDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        addBinding.buttonIssueAddComment.setOnTouchListener { _, event ->

            when {
                addBinding.issueComment.text.isNullOrEmpty() -> {
                    Toast.makeText(activity, "Comment cannot be left empty", Toast.LENGTH_LONG).show()
                }

                else -> {
                    mIssueDetailViewModel.addIssueComments((activity as WorkspaceActivity).workspace_id!!,
                        issue_id.toInt(),
                        addBinding.issueComment.text.toString(),
                        (activity as WorkspaceActivity).token!!)
                    addCommentDialog.dismiss()

                }
            }

            true


        }
    }






}
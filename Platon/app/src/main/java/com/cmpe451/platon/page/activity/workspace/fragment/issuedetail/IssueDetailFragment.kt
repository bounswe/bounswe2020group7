package com.cmpe451.platon.page.activity.workspace.fragment.issuedetail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.AssigneeAdapter
import com.cmpe451.platon.adapter.CollaboratorAdapter
import com.cmpe451.platon.adapter.CommentsAdapter
import com.cmpe451.platon.adapter.IssuesAdapter
import com.cmpe451.platon.databinding.*
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Assignee
import com.cmpe451.platon.network.models.Comment
import com.cmpe451.platon.network.models.Issue
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.util.Definitions
import com.google.android.play.core.assetpacks.t
import java.util.*

class IssueDetailFragment: Fragment() {
    private lateinit var dialog: AlertDialog
    private val mIssueDetailViewModel: IssueDetailViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var issue_id: String
    lateinit var issue_title: String
    lateinit var issue_description: String
    lateinit var issue_creator_name: String
    lateinit var issue_deadline: String


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
        issue_id = sharedPreferences.getString("issue_id", null).toString()
        issue_title = sharedPreferences.getString("issue_title", null).toString()
        issue_description = sharedPreferences.getString("issue_description", null).toString()
        issue_creator_name = sharedPreferences.getString("issue_creator_name", null).toString()
        issue_deadline = sharedPreferences.getString("issue_deadline", null).toString()
        initViews()
        setListeners()
        setObservers()
        mIssueDetailViewModel.getIssueAssignee((activity as WorkspaceActivity).workspace_id!!,issue_id.toInt(), null, null, (activity as WorkspaceActivity).token!!)
        mIssueDetailViewModel.fetchWorkspace((activity as WorkspaceActivity).workspace_id!!, (activity as WorkspaceActivity).token!!)
    }





    private fun setObservers(){


        mIssueDetailViewModel.assigneeResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    (binding.issueAssignee.adapter as AssigneeAdapter).submitElements(t.data!!.result)

                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Loading::class.java -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
            }
        })







    }
    private fun initViews() {

        val height = resources.displayMetrics.heightPixels
        val width = resources.displayMetrics.widthPixels
        binding.issueTitle.text = issue_title
        binding.issueDescriptionTextView.text = issue_description
        binding.issueCreatorName.text = issue_creator_name

        binding.issueAssignee.adapter = AssigneeAdapter(ArrayList(), requireContext())
        binding.issueAssignee.layoutManager = LinearLayoutManager(requireContext())

        /*
        issueRecyclerView = binding.issuesRecyclerView
        val layoutManagerIssues = LinearLayoutManager(this.activity)
        issueRecyclerView.layoutManager = layoutManagerIssues
        adapter = IssuesAdapter(ArrayList(),requireContext(), this)
        issueRecyclerView.adapter = adapter
         */




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
                    //getComments
                    binding.issueCommentsRecyclerView.adapter = CommentsAdapter(arrayListOf(Comment(0,"hehe", "haha", "today", 2.5)), requireContext())
                    binding.issueCommentsRecyclerView.visibility = View.VISIBLE
                }
                View.VISIBLE->{
                    binding.issueCommentsRecyclerView.visibility = View.GONE
                }
            }

        }
    }



    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.issue_btn)?.isVisible = false
        menu.findItem(R.id.button_issue_add)?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    private fun onAddIssueButtonClicked() {
        /*
        val addBinding = DialogAddIssueBinding.inflate(layoutInflater, binding.root, false)
        val addDialog = AlertDialog.Builder(requireContext())
            .setView(addBinding.root)
            .show()
        addDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        addBinding.buttonIssueAdd.setOnClickListener {
            // check if the title and year is empty
            if(addBinding.issueTitle.text.isNullOrEmpty() && addBinding.issueDescription.text.isNullOrEmpty()){
                Toast.makeText(activity as WorkspaceActivity, "Title and Description cannot be left empty", Toast.LENGTH_LONG).show()
            }
            else {
                when {
                    addBinding.issueTitle.text.isNullOrEmpty() -> {
                        Toast.makeText(activity, "Title cannot be left empty", Toast.LENGTH_LONG).show()
                    }
                    addBinding.issueDescription.text.isNullOrEmpty() -> {
                        Toast.makeText(activity , "Description cannot be left empty", Toast.LENGTH_LONG).show()
                    }
                    else -> {

                        mIssueDetailViewModel.addIssues((activity as WorkspaceActivity).workspace_id!!, addBinding.issueTitle.text.toString(),addBinding.issueDescription.text.toString(),addBinding.issueDeadline.text.toString(), (activity as WorkspaceActivity).token!!)
                        addDialog.dismiss()
                    }
                }
            }
        }
         */

    }



    @SuppressLint("ClickableViewAccessibility")
    private fun onUpdateButtonClicked() {
        val tmpBinding = FragmentEditIssueBinding.inflate(
            layoutInflater,
            requireView().parent as ViewGroup,
            false
        )

        tmpBinding.issueDeadlineEt.setText(issue_deadline)
        tmpBinding.issueDescriptionEt.setText(issue_description)
        tmpBinding.issueTitleEt.setText(issue_title)

        tmpBinding.issueDeadlineEt.setOnTouchListener { _, event ->

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
                        tmpBinding.issueDeadlineEt.setText("$years-$monthString-$dayString")
                    }, year, month, dayOfMonth

                )

                datePickerDialog.show()
            }
            true


        }


        AlertDialog.Builder(context).setView(tmpBinding.root)
            .setCancelable(true)
            .create().show()

        tmpBinding.deleteIssueBtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage("Are you sure you want to delete? ")
                .setPositiveButton(
                    "Delete"
                ) { _, _ ->
                    //TODO: Delete issue
                    //mWorkspaceViewModel.deleteWorkspace((activity as WorkspaceActivity).workspace_id!!,(activity as WorkspaceActivity).token!!)
                    mIssueDetailViewModel.deleteIssue((activity as WorkspaceActivity).workspace_id!!, issue_id.toInt(), (activity as WorkspaceActivity).token!!)
                    findNavController()
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
                    val deadline =
                        if (tmpBinding.issueDeadlineEt.text.isNullOrEmpty()) null else tmpBinding.issueDeadlineEt.text.toString()
                    //TODO: update workspace
                    //mWorkspaceViewModel.updateWorkspace((activity as WorkspaceActivity).workspace_id!!)

                }
            }


        }
    }

    fun onAddAssigneeClicked() {


    }






}
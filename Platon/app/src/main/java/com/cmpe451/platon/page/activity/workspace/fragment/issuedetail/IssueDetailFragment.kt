package com.cmpe451.platon.page.activity.workspace.fragment.issuedetail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.CommentsAdapter
import com.cmpe451.platon.adapter.IssuesAdapter
import com.cmpe451.platon.databinding.*
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Comment
import com.cmpe451.platon.network.models.Issue
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.util.Definitions
import java.util.*

class IssueDetailFragment: Fragment() {
    private lateinit var dialog: AlertDialog
    private val mIssueDetailViewModel: IssueDetailViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

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
        initViews()
        setListeners()
        setObservers()
        //mIssueDetailViewModel.getIssues((activity as WorkspaceActivity).workspace_id!!,0, maxPageNumberIssue, (activity as WorkspaceActivity).token!!)

    }





    private fun setObservers(){
        /*
         mIssueDetailViewModel.issuesResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java ->{

                    val issue = t.data!!.result as ArrayList<Issue>
                    //adapter.addElement(0, issue[0])
                    adapter.submitElements(issue)
                    //var issueArray = issue
                    //TODO: add element

                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
         */


    }
    private fun initViews() {

        val height = resources.displayMetrics.heightPixels
        val width = resources.displayMetrics.widthPixels

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
    private fun onUpdateButtonClicked(){
        val tmpBinding = FragmentEditIssueBinding.inflate(
            layoutInflater,
            requireView().parent as ViewGroup,
            false
        )
        /*
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

         */
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
                        tmpBinding.issueDeadlineEt.setText("$years.$monthString.$dayString")
                    }, year, month, dayOfMonth

                )

                datePickerDialog.show()
            }
            true


        }
        AlertDialog.Builder(context).setView(tmpBinding.root)
            .setCancelable(true)
            .setNeutralButton("Delete Issue") { _: DialogInterface, _: Int ->
                AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to delete ?")
                    .setPositiveButton("Delete"
                    ) { _, _ ->
                        val ert = 0
                        /*
                        mWorkspaceViewModel.deleteWorkspace(
                            (activity as WorkspaceActivity).workspace_id!!,
                            (activity as WorkspaceActivity).token!!
                        )

                         */
                    }
                    .setNegativeButton("Cancel"
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create().show()
            }
            .setNegativeButton("Update", DialogInterface.OnClickListener { dialog, _ ->
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

                        val ert = 0
                        /*
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

                         */
                    }
                }
            })

            .create().show()

    }






}
package com.cmpe451.platon.page.activity.workspace.fragment.issuedetail

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.IssuesAdapter
import com.cmpe451.platon.databinding.DialogAddIssueBinding
import com.cmpe451.platon.databinding.FragmentIssueDetailBinding
import com.cmpe451.platon.databinding.IssueCellBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Issue
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.util.Definitions

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



}
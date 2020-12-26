/*
package com.cmpe451.platon.page.activity.workspace.fragment.issues

class IssuesFragment {
}
*/

package com.cmpe451.platon.page.activity.workspace.fragment.issues

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.IssuesAdapter
import com.cmpe451.platon.databinding.*
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Issue
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.util.Definitions

class IssuesFragment : Fragment(),IssuesAdapter.IssuesButtonClickListener {

    //definitions
    private lateinit var dialog: AlertDialog
    private val mIssuesViewModel:IssuesViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    lateinit var binding: FragmentIssuesBinding
    private lateinit var adapter: IssuesAdapter
    private lateinit var issueRecyclerView: RecyclerView

    lateinit var issue: ArrayList<Issue>

    private var maxPageNumberIssue:Int=10


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentIssuesBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        sharedPreferences = activity?.getSharedPreferences("token_file", Context.MODE_PRIVATE)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setListeners()
        setObservers()
        mIssuesViewModel.getIssues((activity as WorkspaceActivity).workspace_id!!,0, maxPageNumberIssue, (activity as WorkspaceActivity).token!!)

    }





    private fun setObservers(){
        mIssuesViewModel.issuesResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java ->{

                    issue = t.data!!.result as ArrayList<Issue>
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

    }
    private fun initViews() {

        val height = resources.displayMetrics.heightPixels
        val width = resources.displayMetrics.widthPixels

        issueRecyclerView = binding.issuesRecyclerView
        val layoutManagerIssues = LinearLayoutManager(this.activity)
        issueRecyclerView.layoutManager = layoutManagerIssues
        adapter = IssuesAdapter(ArrayList(),requireContext(), this)
        issueRecyclerView.adapter = adapter

        //binding.issuesRecyclerView.layoutManager = layoutManagerIssues
        //binding.issuesRecyclerView.adapter = IssuesAdapter(ArrayList(),requireContext(), this)
        /*
        binding.issuesRecyclerView.addOnScrollListener(object: PaginationListener(layoutManagerIssues){
            override fun loadMoreItems() {
                /*
                if(maxPageNumberIssue-1 > currentPage){
                    currentPage++
                    mIssuesViewModel.getIssues(1,1,1,"")
                    Toast.makeText(requireContext(), "Next page", Toast.LENGTH_LONG).show()
                }

                 */
            }

            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        })

         */

        //binding.issuesRecyclerView.layoutParams = LinearLayout.LayoutParams(width, (height/2))


    }


    private fun setListeners() {
        dialog = Definitions().createProgressBar(requireContext())
    }


    override fun onIssueButtonClicked(binding: IssueCellBinding, position: Int) {
        findNavController().navigate(IssuesFragmentDirections.actionIssuesFragmentToIssueDetailFragment())
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.issue_btn)?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    private fun onAddIssueButtonClicked() {
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

                        mIssuesViewModel.addIssues((activity as WorkspaceActivity).workspace_id!!, addBinding.issueTitle.text.toString(),addBinding.issueDescription.text.toString(),addBinding.issueDeadline.text.toString(), (activity as WorkspaceActivity).token!!)
                        addDialog.dismiss()
                    }
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_issue_btn -> onAddIssueButtonClicked()
        }

        return super.onOptionsItemSelected(item)
    }
}


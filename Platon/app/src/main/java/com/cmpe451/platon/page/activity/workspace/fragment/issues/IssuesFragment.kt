/*
package com.cmpe451.platon.page.activity.workspace.fragment.issues

class IssuesFragment {
}
*/

package com.cmpe451.platon.page.activity.workspace.fragment.issues

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.IssuesAdapter
import com.cmpe451.platon.databinding.*
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Issue
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.util.Definitions
import java.util.*
import kotlin.collections.ArrayList

class IssuesFragment : Fragment(),IssuesAdapter.IssuesButtonClickListener {

    //definitions
    private lateinit var dialog: AlertDialog
    private val mIssuesViewModel:IssuesViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    lateinit var binding: FragmentIssuesBinding
    private val issuesList: ArrayList<Issue> = arrayListOf()

    private lateinit var paginationListener:PaginationListener
    private var maxPageNumberIssue:Int=0
    private var pageSize:Int=10



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
        issuesList.clear()
        mIssuesViewModel.getIssues((activity as WorkspaceActivity).workspace_id!!,0, pageSize, (activity as WorkspaceActivity).token!!)

    }

    


    private fun setObservers(){

        mIssuesViewModel.issuesResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Loading::class.java->{
                    dialog.show()
                }
                Resource.Success::class.java ->{
                    //(binding.issuesRecyclerView.adapter as IssuesAdapter).clearElements()
                    val issue = t.data!!.result as ArrayList<Issue>
                    maxPageNumberIssue = t.data!!.number_of_pages
                    (binding.issuesRecyclerView.adapter as IssuesAdapter).submitElements(issue)
                    issuesList.addAll(issue)
                    mIssuesViewModel.issuesResponse.value = Resource.Done()
                    paginationListener.isLoading = false
                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    mIssuesViewModel.issuesResponse.value = Resource.Done()
                }
                Resource.Done::class.java->{
                    dialog.dismiss()
                }
            }
        })


        mIssuesViewModel.addIssuesResourceResponse.observe(viewLifecycleOwner, {
            when(it.javaClass){
                Resource.Loading::class.java->{
                    dialog.show()
                }
                Resource.Success::class.java ->{
                    paginationListener.currentPage = 0
                    issuesList.clear()
                    (binding.issuesRecyclerView.adapter as IssuesAdapter).clearElements()
                    mIssuesViewModel.getIssues((activity as WorkspaceActivity).workspace_id!!, 0, pageSize,(activity as WorkspaceActivity).token!! )
                    mIssuesViewModel.issuesResponse.value = Resource.Done()
                }
                Resource.Error::class.java ->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    mIssuesViewModel.issuesResponse.value = Resource.Done()
                }
                Resource.Done::class.java->{
                    dialog.dismiss()
                }
            }

        })



    }
    private fun initViews() {

        val layoutManagerIssues = LinearLayoutManager(this.activity)

        paginationListener = object: PaginationListener(layoutManagerIssues, pageSize){
            override fun loadMoreItems() {
                if(maxPageNumberIssue-1 > currentPage){
                    isLoading = true
                    currentPage++
                    mIssuesViewModel.getIssues((activity as WorkspaceActivity).workspace_id!!, currentPage, PAGE_SIZE,(activity as WorkspaceActivity).token!!)
                }
            }

            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        }


        binding.issuesRecyclerView.layoutManager = layoutManagerIssues
        binding.issuesRecyclerView.adapter = IssuesAdapter(ArrayList(),requireContext(), this)

        binding.issuesRecyclerView.addOnScrollListener(paginationListener)
    }


    private fun setListeners() {
        dialog = Definitions().createProgressBar(requireContext())
    }


    override fun onIssueButtonClicked(binding: IssueCellBinding, position: Int) {
        sharedPreferences.edit().putString("issue_title", issuesList[position].title).apply()
        sharedPreferences.edit().putString("issue_description", issuesList[position].description).apply()
        sharedPreferences.edit().putString("issue_id", issuesList[position].issue_id.toString()).apply()
        sharedPreferences.edit().putString("issue_deadline", issuesList[position].deadline).apply()
        sharedPreferences.edit().putString("issue_creator_name", issuesList[position].creator_name + " " + issuesList[position].creator_surname).apply()
        findNavController().navigate(IssuesFragmentDirections.actionİssuesFragmentToİssueDetailFragment())
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.issue_btn)?.isVisible = false
        menu.findItem(R.id.btn_WorkspaceApplications)?.isVisible = false
        menu.findItem(R.id.workspaceFolderFragment)?.isVisible = false

        super.onPrepareOptionsMenu(menu)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onAddIssueButtonClicked() {
        val addBinding = DialogAddIssueBinding.inflate(layoutInflater, binding.root, false)
        val addDialog = AlertDialog.Builder(requireContext())
            .setView(addBinding.root)
            .show()
        addDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        addBinding.issueDeadline.setOnTouchListener { _, event ->

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
                        addBinding.issueDeadline.setText("$years-$monthString-$dayString")
                    }, year, month, dayOfMonth

                )

                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                datePickerDialog.setOnDismissListener {
                    val timePickerDialog = TimePickerDialog(requireContext(), {
                       _, hour , minute ->

                        addBinding.issueDeadline.setText(addBinding.issueDeadline.text.toString().trim() + " $hour:$minute:00")
                    }, hour, minute, true )
                    timePickerDialog.show()

                }

                datePickerDialog.show()
            }
            true


        }



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
                    addBinding.issueDeadline.text.isNullOrEmpty() -> {
                        Toast.makeText(activity , "Deadline cannot be left empty", Toast.LENGTH_LONG).show()
                    }

                    else -> {

                        mIssuesViewModel.addIssues((activity as WorkspaceActivity).workspace_id!!,
                            addBinding.issueTitle.text.toString(),
                            addBinding.issueDescription.text.toString(),
                            addBinding.issueDeadline.text.toString(),
                            (activity as WorkspaceActivity).token!!)
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


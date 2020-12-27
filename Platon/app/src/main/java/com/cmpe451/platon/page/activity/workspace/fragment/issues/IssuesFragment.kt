/*
package com.cmpe451.platon.page.activity.workspace.fragment.issues

class IssuesFragment {
}
*/

package com.cmpe451.platon.page.activity.workspace.fragment.issues

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
                    adapter.clearElements()
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

        mIssuesViewModel.addIssuesResourceResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    mIssuesViewModel.getIssues((activity as WorkspaceActivity).workspace_id!!,0, maxPageNumberIssue, (activity as WorkspaceActivity).token!!)
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
        sharedPreferences.edit().putString("issue_title", issue[position].title).apply()
        sharedPreferences.edit().putString("issue_description", issue[position].description).apply()
        sharedPreferences.edit().putString("issue_id", issue[position].issue_id.toString()).apply()
        sharedPreferences.edit().putString("issue_deadline", issue[position].deadline).apply()
        sharedPreferences.edit().putString("issue_creator_name", issue[position].creator_name + " " + issue[position].creator_surname).apply()
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

                datePickerDialog.show()
            }
            true


        }

        addBinding.issueTimeEt.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val datePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hours, minutes ->
                        val h = String.format("%02d", hours)
                        val m = String.format("%02d", minutes)
                        addBinding.issueTimeEt.setText("$h:$m:00")
                    }, hour, minute, true

                )

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
                    addBinding.issueDeadline.text.isNotEmpty() && addBinding.issueTimeEt.text.isNullOrEmpty() -> {
                        Toast.makeText(activity , "Time cannot be left empty", Toast.LENGTH_LONG).show()
                    }
                    addBinding.issueDeadline.text.isNullOrEmpty() && addBinding.issueTimeEt.text.isNotEmpty() -> {
                        Toast.makeText(activity , "Date cannot be left empty", Toast.LENGTH_LONG).show()
                    }
                    addBinding.issueDeadline.text.isNullOrEmpty() && addBinding.issueTimeEt.text.isNullOrEmpty() -> {
                        Toast.makeText(activity , "Date and time cannot be left empty", Toast.LENGTH_LONG).show()
                    }
                    else -> {

                        mIssuesViewModel.addIssues((activity as WorkspaceActivity).workspace_id!!,
                            addBinding.issueTitle.text.toString(),
                            addBinding.issueDescription.text.toString(),
                            addBinding.issueDeadline.text.toString() + " " + addBinding.issueTimeEt.text.toString(),
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


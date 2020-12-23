/*
package com.cmpe451.platon.page.activity.workspace.fragment.issues

class IssuesFragment {
}
*/
package com.cmpe451.platon.page.activity.workspace.fragment.issues

import com.cmpe451.platon.page.activity.login.fragment.landing.LandingFragmentDirections
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmpe451.platon.adapter.IssuesAdapter
import com.cmpe451.platon.adapter.TrendingProjectsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.*
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Issue
import com.cmpe451.platon.network.models.Issues
import com.cmpe451.platon.network.models.TrendingProject
import com.cmpe451.platon.util.Definitions

class IssuesFragment : Fragment(),IssuesAdapter.IssuesButtonClickListener {

    //definitions
    private lateinit var dialog: AlertDialog
    private val mIssuesViewModel:IssuesViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    lateinit var binding: FragmentIssuesBinding

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
        mIssuesViewModel.getIssues(1,1,1,"")

    }

    private fun doAutoLogin():Boolean{
        val mail = sharedPreferences.getString("mail", null)
        val pass = sharedPreferences.getString("pass", null)
        if(mail != null && pass != null){
            findNavController().navigate(LandingFragmentDirections.actionLandingFragmentToLoginFragment())
            return true
        }
        return false
    }

    private fun setObservers(){
        mIssuesViewModel.issuesResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Success::class.java ->{
                    val issue = t.data!!.issues as Issues
                    var issueArray = issue.issues
                    binding.issuesRecyclerView.adapter =
                        IssuesAdapter(issueArray, requireContext(), this)

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

        val layoutManagerIssues = LinearLayoutManager(context)

        binding.issuesRecyclerView.layoutManager = layoutManagerIssues
        binding.issuesRecyclerView.adapter = IssuesAdapter(ArrayList(),requireContext(), this)
        binding.issuesRecyclerView.addOnScrollListener(object: PaginationListener(layoutManagerIssues){
            override fun loadMoreItems() {
                if(maxPageNumberIssue-1 > currentPage){
                    currentPage++
                    mIssuesViewModel.getIssues(1,1,1,"")
                    Toast.makeText(requireContext(), "Next page", Toast.LENGTH_LONG).show()
                }
            }

            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        })

        binding.issuesRecyclerView.layoutParams = LinearLayout.LayoutParams(width, (height/2))


    }


    private fun setListeners() {
        dialog = Definitions().createProgressBar(requireContext())
    }


    override fun onIssueButtonClicked(binding: IssueCellBinding, position: Int) {
        /*
        if (binding.issueDescription.visibility == View.GONE){
            binding.issueDescription.visibility = View.VISIBLE
        }else{
            binding.issueDescription.visibility = View.GONE
        }

        binding.issueDescription.refreshDrawableState()
        Definitions().vibrate(50, activity as BaseActivity)

         */
    }
}
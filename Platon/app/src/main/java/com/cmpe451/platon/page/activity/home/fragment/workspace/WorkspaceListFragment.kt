package com.cmpe451.platon.page.activity.home.fragment.workspace

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.WorkspaceListAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.FragmentWorkspaceListBinding
import com.cmpe451.platon.databinding.WorkspaceCellBinding
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.page.activity.home.fragment.profilepage.ProfilePageViewModel
import com.cmpe451.platon.util.Definitions

class WorkspaceListFragment : Fragment(), WorkspaceListAdapter.WorkspaceListButtonClickListener{

    private lateinit var binding: FragmentWorkspaceListBinding
    private val mWorkspaceListViewModel: WorkspaceListViewModel by viewModels()
    private val mProfilePageViewModel : ProfilePageViewModel by activityViewModels()
    private lateinit var dialog: AlertDialog

    private lateinit var workspaceListAdapter: WorkspaceListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentWorkspaceListBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
        fetchWorkspaces()
    }

    private fun setListeners() {
        workspaceListAdapter = WorkspaceListAdapter(ArrayList(), requireContext(), this)
        val layoutManager = LinearLayoutManager(activity)
        binding.workspaceListRv.layoutManager = layoutManager
        binding.workspaceListRv.adapter = workspaceListAdapter

//        binding.workspaceListRv.addOnScrollListener(object: PaginationListener(layoutManager){
//            override fun loadMoreItems() {
//                //isLoading = true;
//                currentPage++;
//
//            }
//
//            override var isLastPage: Boolean = false
//            override var isLoading: Boolean = false
//            override var currentPage: Int = PAGE_START
//
//        })
    }

    private fun setObservers(){
        dialog = Definitions().createProgressBar(requireContext())
        mWorkspaceListViewModel.workspaces.observe(viewLifecycleOwner, Observer {
            when(it.javaClass){
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    workspaceListAdapter.submitElements(it.data!!.workspaces)
                }
            }
        })
    }

    private fun fetchWorkspaces(){
        mWorkspaceListViewModel.getWorkspaces((activity as HomeActivity).token!!)
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.logout_menu_btn)?.isVisible = false
        menu.findItem(R.id.add_workspace_btn)?.isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onWorkspaceListButtonClicked(binding: WorkspaceCellBinding, projectId: Int) {
        val bnd = Bundle()
        bnd.putString("token", (activity as HomeActivity).token!!)
        bnd.putInt("user_id", (activity as HomeActivity).userId!!)
        bnd.putBoolean("add", false)
        bnd.putInt("workspace_id", projectId)
        startActivity(Intent(activity, WorkspaceActivity::class.java).putExtras(bnd))
    }

    override fun onResume() {
        workspaceListAdapter.clearElements()
        mWorkspaceListViewModel.getWorkspaces((activity as HomeActivity).token!!)
        super.onResume()
    }
}
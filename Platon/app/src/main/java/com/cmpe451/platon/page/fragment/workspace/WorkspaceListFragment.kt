package com.cmpe451.platon.page.fragment.workspace

import android.app.AlertDialog
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
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.profilepage.ProfilePageViewModel
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
        workspaceListAdapter = WorkspaceListAdapter(ArrayList(), requireContext(), this)
        binding.workspaceListRv.layoutManager = LinearLayoutManager(activity)
        binding.workspaceListRv.adapter = workspaceListAdapter
        setObservers()
        fetchWorkspaces()
    }

    private fun setObservers(){
        dialog = Definitions().createProgressBar(requireContext())
        mWorkspaceListViewModel.workspaces.observe(viewLifecycleOwner, Observer {
            when(it.javaClass){
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    workspaceListAdapter.submitElements(it.data!!)
                }
            }
        })
    }
    private fun fetchWorkspaces(){
        if(mProfilePageViewModel.getUserResourceResponse.value?.data?.id != null){
            mWorkspaceListViewModel.getWorkspaces(mProfilePageViewModel.getUserResourceResponse.value?.data?.id!!, (activity as HomeActivity).token!!)
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        // clear search bar, and make it icon
        val search = (menu.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setQuery("", false)
        search.isIconified = true

        menu.findItem(R.id.registerFragment)?.isVisible = false
        menu.findItem(R.id.loginFragment)?.isVisible = false
        menu.findItem(R.id.search_btn)?.isVisible = false
        menu.findItem(R.id.notification_btn)?.isVisible = false
    }

    override fun onWorkspaceListButtonClicked(binding: WorkspaceCellBinding, position: Int) {
        if (binding.descWorkspaceTv.visibility == View.GONE){
            binding.descWorkspaceTv.visibility = View.VISIBLE
        }else{
            binding.descWorkspaceTv.visibility = View.GONE
        }

        binding.descWorkspaceTv.refreshDrawableState()
        Definitions().vibrate(50, activity as BaseActivity)
    }

    override fun onWorkspaceListEditClicked(position: Int) {
        TODO("Not yet implemented")
    }
}
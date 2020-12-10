package com.cmpe451.platon.page.fragment.workspace

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.WorkspaceListAdapter
import com.cmpe451.platon.databinding.FragmentWorkspaceListBinding
import com.cmpe451.platon.databinding.WorkspaceCellBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Workspace

class WorkspaceListFragment : Fragment(), WorkspaceListAdapter.WorkspaceListButtonClickListener {

    private lateinit var binding: FragmentWorkspaceListBinding
    private lateinit var dialog: AlertDialog
    private lateinit var workpaceListAdaper:WorkspaceListAdapter
    private val mWorkspaceListViewModel = WorkspaceListViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentWorkspaceListBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapter()
        setObservers()
    }

    private fun initializeAdapter(){
        workpaceListAdaper = WorkspaceListAdapter(ArrayList(), requireContext(), this)
        binding.workspaceListRv.adapter = workpaceListAdaper
    }
    private fun setObservers(){
        mWorkspaceListViewModel.workspace.observe(viewLifecycleOwner, Observer{
            when(it.javaClass) {
                Resource.Success::class.java -> {
                    workpaceListAdaper.submitElements(it.data as ArrayList<Workspace>)
                    dialog.dismiss()
                }
                Resource.Error::class.java -> dialog.show()
                Resource.Error::class.java -> {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })

    }



    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val search = (menu.findItem(R.id.search_btn)?.actionView as SearchView)
        search.setQuery("", false)
        search.isIconified = true

        menu.findItem(R.id.notification_btn)?.isVisible = false
        menu.findItem(R.id.registerFragment)?.isVisible = false
        menu.findItem(R.id.loginFragment)?.isVisible = false
        menu.findItem(R.id.search_btn)?.isVisible = false
    }

    override fun onWorkspaceListButtonClicked(binding: WorkspaceCellBinding, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onWorkspaceListEditClicked(position: Int) {
        TODO("Not yet implemented")
    }
}


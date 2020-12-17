package com.cmpe451.platon.page.activity.workspace.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.R
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity

class WorkspaceFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workspace, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if((activity as WorkspaceActivity).addClicked!!){
            (activity as WorkspaceActivity).addClicked = false
            findNavController().navigate(WorkspaceFragmentDirections.actionWorkspaceFragmentToAddWorkspaceFragment())
        }
        super.onViewCreated(view, savedInstanceState)
    }

}
package com.cmpe451.platon.page.activity.workspace.fragment.filesystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cmpe451.platon.databinding.FragmentWorkspaceFolderBinding

class WorkspaceFolderFragment :Fragment() {

    lateinit var binding:FragmentWorkspaceFolderBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentWorkspaceFolderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
//        menu.findItem(R.)
        super.onPrepareOptionsMenu(menu)
    }

}
package com.cmpe451.platon.page.activity.workspace.fragment.filesystem

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.FilesAdapter
import com.cmpe451.platon.adapter.FoldersAdapter
import com.cmpe451.platon.databinding.AddRequirementBinding
import com.cmpe451.platon.databinding.DialogAddFolderBinding
import com.cmpe451.platon.databinding.FragmentWorkspaceFolderBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.util.Definitions

class WorkspaceFolderFragment :Fragment(), FoldersAdapter.FoldersButtonClickListener, FilesAdapter.FilesButtonClickListener {

    lateinit var binding:FragmentWorkspaceFolderBinding
    private lateinit var cwd:String
    private lateinit var dialog:AlertDialog



    private val mWorkspaceFolderViewModel:WorkspaceFolderViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentWorkspaceFolderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapters()
        setListeners()
        setObservers()
        setView(ArrayList(), ArrayList(), ".")
        getFolder(cwd)
        setHasOptionsMenu(true)

    }

    private fun setObservers() {
        mWorkspaceFolderViewModel.getFolderResourceResponse.observe(viewLifecycleOwner, {
            when(it.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    setView(it.data!!.folders, it.data!!.files, it.data!!.cwd)
                    mWorkspaceFolderViewModel.getFolderResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java ->{
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    mWorkspaceFolderViewModel.getFolderResourceResponse.value = Resource.Done()

                }
                Resource.Done::class.java->{
                    dialog.dismiss()
                }
            }
        })
        mWorkspaceFolderViewModel.getAddUpdateDeleteFolderResourceResponse.observe(viewLifecycleOwner, {
            when(it.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    getFolder(cwd)
                    mWorkspaceFolderViewModel.getFolderResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java ->{
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    mWorkspaceFolderViewModel.getFolderResourceResponse.value = Resource.Done()

                }
                Resource.Done::class.java->{
                    dialog.dismiss()
                }
            }
        })
    }

    private fun setListeners() {
        binding.directoryUpTv.setOnClickListener {
            getFolder("$cwd/..")
        }
        binding.foldersTitleTv.setOnClickListener {
            val tmpBinding = DialogAddFolderBinding.inflate(
                layoutInflater,
                requireView().parent as ViewGroup,
                false
            )
            val addFolderDialog = AlertDialog.Builder(context).setView(tmpBinding.root)
                .setCancelable(true)
                .show()
            tmpBinding.btnAddFolder.setOnClickListener {
                if (tmpBinding.etNewFolderName.text.isNullOrEmpty()) {
                    addFolderDialog.dismiss()
                }
                else {
                    addFolderDialog.dismiss()
                    postFolder(tmpBinding.etNewFolderName.text.toString().trim())
                }
            }
        }
    }



    private fun setView(folders:List<String>, files:List<String>, cwd:String) {
        this.cwd = cwd
        if(this.cwd == "."){
            binding.directoryUpTv.visibility = View.GONE
        }
        else {
            binding.directoryUpTv.visibility = View.VISIBLE
        }
        (binding.rvWorkspaceFolders.adapter as FoldersAdapter).replaceElements(folders)
        (binding.rvWorkspaceFiles.adapter as FilesAdapter).replaceElements(files)

    }

    private fun initializeAdapters() {
        dialog = Definitions().createProgressBar(requireContext())
        binding.rvWorkspaceFolders.adapter = FoldersAdapter(ArrayList(), requireContext(), this)
        binding.rvWorkspaceFolders.layoutManager = LinearLayoutManager(requireContext())

        binding.rvWorkspaceFiles.adapter = FilesAdapter(ArrayList(), requireContext(), this)
        binding.rvWorkspaceFiles.layoutManager = LinearLayoutManager(requireContext())
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.workspaceFolderFragment).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onEditFolderClicked(folder: String) {
        val tmpBinding = DialogAddFolderBinding.inflate(
            layoutInflater,
            requireView().parent as ViewGroup,
            false
        )
        val editFolderDialog = AlertDialog.Builder(context).setView(tmpBinding.root)
            .setCancelable(true)
            .show()
        tmpBinding.etNewFolderName.setText(folder)
        tmpBinding.btnAddFolder.setOnClickListener {
            if (tmpBinding.etNewFolderName.text.isNullOrEmpty()) {
                editFolderDialog.dismiss()
            }
            else {
                editFolderDialog.dismiss()
                updateFolderName(folder,tmpBinding.etNewFolderName.text.toString().trim())
            }
        }
    }

    override fun onFolderNameClicked(folder: String) {
        getFolder("${this.cwd}/$folder")
    }

    override fun onDeleteFolderClicked(folder: String) {
        AlertDialog.Builder(context)
            .setMessage("Are you sure you want to delete folder $folder? All content within the folder will be lost.")
            .setPositiveButton("Delete"
            ) { _, _ ->
                deleteFolder(folder)
            }
            .setNegativeButton("Cancel"
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }

    private fun getFolder(path:String){
        mWorkspaceFolderViewModel.getFolder((activity as WorkspaceActivity).workspace_id!!, path, (activity as WorkspaceActivity).token!!)
    }
    private fun postFolder(folderName: String) {
        mWorkspaceFolderViewModel.addFolder((activity as WorkspaceActivity).workspace_id!!,
            cwd, folderName, (activity as WorkspaceActivity).token!!)
    }
    private fun updateFolderName(oldFolderName:String,folderName: String){
        mWorkspaceFolderViewModel.updateFolderName((activity as WorkspaceActivity).workspace_id!!, "$cwd/$oldFolderName", folderName, (activity as WorkspaceActivity).token!!)
    }
    private fun deleteFolder(folderName:String){
        mWorkspaceFolderViewModel.deleteFolder((activity as WorkspaceActivity).workspace_id!!, "$cwd/$folderName", folderName, (activity as WorkspaceActivity).token!!)
    }

    override fun onEditFileClicked(folder: String) {
        TODO("Not yet implemented")
    }

    override fun onFileNameClicked(folder: String) {
        TODO("Not yet implemented")
    }

    override fun onDeleteFileClicked(folder: String) {
        TODO("Not yet implemented")
    }

}
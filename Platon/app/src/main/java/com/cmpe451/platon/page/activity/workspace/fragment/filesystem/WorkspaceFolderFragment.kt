package com.cmpe451.platon.page.activity.workspace.fragment.filesystem

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.FilesAdapter
import com.cmpe451.platon.adapter.FoldersAdapter
import com.cmpe451.platon.databinding.DialogAddFolderBinding
import com.cmpe451.platon.databinding.FragmentWorkspaceFolderBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.util.Definitions
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.utils.ContentUriUtils
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File


class WorkspaceFolderFragment :Fragment(), FoldersAdapter.FoldersButtonClickListener, FilesAdapter.FilesButtonClickListener {

    lateinit var binding:FragmentWorkspaceFolderBinding
    private lateinit var cwd:String
    private lateinit var dialog:AlertDialog



    private val mWorkspaceFolderViewModel:WorkspaceFolderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWorkspaceFolderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapters()
        setListeners()
        setObservers()
        initView()
        setView(ArrayList(), ArrayList(), ".")
        getFolder(cwd)
        setHasOptionsMenu(true)

    }

    private fun initView() {
        if(!(activity as WorkspaceActivity).isOwner!!){
            binding.filesTitleTv.setCompoundDrawables(null, null, null, null)
            binding.foldersTitleTv.setCompoundDrawables(null, null, null, null)
        }

    }

    private fun setObservers() {
        mWorkspaceFolderViewModel.getFolderResourceResponse.observe(viewLifecycleOwner, {
            when (it.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    setView(it.data!!.folders, it.data!!.files, it.data!!.cwd)
                    mWorkspaceFolderViewModel.getFolderResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    mWorkspaceFolderViewModel.getFolderResourceResponse.value = Resource.Done()

                }
                Resource.Done::class.java -> {
                    dialog.dismiss()
                }
            }
        })
        mWorkspaceFolderViewModel.getAddUpdateDeleteFolderResourceResponse.observe(
            viewLifecycleOwner,
            {
                when (it.javaClass) {
                    Resource.Loading::class.java -> dialog.show()
                    Resource.Success::class.java -> {
                        getFolder(cwd)
                        mWorkspaceFolderViewModel.getFolderResourceResponse.value = Resource.Done()
                    }
                    Resource.Error::class.java -> {
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                        mWorkspaceFolderViewModel.getFolderResourceResponse.value = Resource.Done()

                    }
                    Resource.Done::class.java -> {
                        dialog.dismiss()
                    }
                }
            })

        mWorkspaceFolderViewModel.getAddFileToWorkspaceResourceResponse.observe(
            viewLifecycleOwner,
            {
                when (it.javaClass) {
                    Resource.Loading::class.java -> dialog.show()
                    Resource.Success::class.java -> {
                        getFolder(cwd)
                        mWorkspaceFolderViewModel.getAddFileToWorkspaceResourceResponse.value =
                            Resource.Done()
                    }
                    Resource.Error::class.java -> {
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                        mWorkspaceFolderViewModel.getAddFileToWorkspaceResourceResponse.value =
                            Resource.Done()

                    }
                    Resource.Done::class.java -> {
                        dialog.dismiss()
                    }
                }
            })

        mWorkspaceFolderViewModel.getAddUpdateDeleteFileResourceResponse.observe(viewLifecycleOwner, {
            when (it.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    getFolder(cwd)
                    mWorkspaceFolderViewModel.getAddUpdateDeleteFileResourceResponse.value =
                        Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    mWorkspaceFolderViewModel.getAddUpdateDeleteFileResourceResponse.value =
                        Resource.Done()

                }
                Resource.Done::class.java -> {
                    dialog.dismiss()
                }
            }


        })



    }

    private fun setListeners() {
        binding.directoryUpTv.setOnClickListener {
            getFolder("$cwd/..")
        }
        if((activity as WorkspaceActivity).isOwner!!){
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
                    } else {
                        addFolderDialog.dismiss()
                        postFolder(tmpBinding.etNewFolderName.text.toString().trim())
                    }
                }
            }

            binding.filesTitleTv.setOnClickListener {

                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PackageManager.PERMISSION_GRANTED
                )

                if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    /*

                    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
                    {
                        if (it.resultCode == Activity.RESULT_OK && it.data != null && it.data!!.data != null) {
                            uploadFile(it.data!!.data!!)
                        }
                    }

                    var chooseFile = Intent(ACTION_GET_CONTENT)
                    chooseFile.type = "*//*"
                chooseFile = Intent.createChooser(chooseFile, "Choose a file")
                startForResult.launch(chooseFile)
                */

                    FilePickerBuilder.instance
                        .setMaxCount(1)
                        .addFileSupport("C", arrayOf("c", "cpp", "C", "CPP"))
                        .addFileSupport("PYTHON", arrayOf("PY", "py"))
                        .addFileSupport("README", arrayOf("md", "MD"))
                        .addFileSupport("PNG", arrayOf("png", "jpg", "jpeg"))
                        .pickFile(this);

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please give read permissions!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode)
           {
               FilePickerConst.REQUEST_CODE_PHOTO->
                   if(resultCode== Activity.RESULT_OK && data!=null)
                   {
                       val ls = data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)
                       if(!ls.isNullOrEmpty()){
                           uploadFile(ls[0])
                       }
                   }
                FilePickerConst.REQUEST_CODE_DOC->
                   if(resultCode== Activity.RESULT_OK && data!=null)
                   {
                       val ls = data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_DOCS)
                       if(!ls.isNullOrEmpty()){
                           uploadFile(ls[0])
                       }
                   }
           }
    }


    private fun uploadFile(data: Uri) {
        val path = ContentUriUtils.getFilePath(requireContext(), data);
        if (path != null) {
            val file = File(path)
            val fBody = RequestBody.create(
                MediaType.parse(requireContext().contentResolver.getType(data)!!),
                file
            )
            val pathBody = RequestBody.create(MediaType.parse("text/plain"), cwd)
            val fileNameBody = RequestBody.create(MediaType.parse("text/plain"), file.name)
            mWorkspaceFolderViewModel.uploadFile(
                (activity as WorkspaceActivity).workspace_id!!,
                pathBody,
                fileNameBody,
                fBody,
                (activity as WorkspaceActivity).token!!
            )
        }
    }


    private fun setView(folders: List<String>, files: List<String>, cwd: String) {
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
        binding.rvWorkspaceFolders.adapter = FoldersAdapter(ArrayList(), this, (activity as WorkspaceActivity).isOwner!!)
        binding.rvWorkspaceFolders.layoutManager = LinearLayoutManager(requireContext())

        binding.rvWorkspaceFiles.adapter = FilesAdapter(ArrayList(), this,(activity as WorkspaceActivity).isOwner!!)
        binding.rvWorkspaceFiles.layoutManager = LinearLayoutManager(requireContext())
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.workspaceFolderFragment).isVisible = false
        menu.findItem(R.id.add_issue_btn).isVisible = false
        menu.findItem(R.id.issue_btn).isVisible = false
        menu.findItem(R.id.btn_WorkspaceApplications).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onEditFolderClicked(folder: String) {
        val tmpBinding = DialogAddFolderBinding.inflate(
            layoutInflater,
            requireView().parent as ViewGroup,
            false
        )
        tmpBinding.btnAddFolder.text = getString(R.string.update_str)
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
                updateFolderName(folder, tmpBinding.etNewFolderName.text.toString().trim())
            }
        }
    }

    override fun onFolderNameClicked(folder: String) {
        getFolder("${this.cwd}/$folder")
    }

    override fun onDeleteFolderClicked(folder: String) {
        AlertDialog.Builder(context)
            .setMessage("Are you sure you want to delete folder $folder? All content within the folder will be lost.")
            .setPositiveButton(
                "Delete"
            ) { _, _ ->
                deleteFolder(folder)
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }

    private fun getFolder(path: String){
        mWorkspaceFolderViewModel.getFolder(
            (activity as WorkspaceActivity).workspace_id!!,
            path,
            (activity as WorkspaceActivity).token!!
        )
    }
    private fun postFolder(folderName: String) {
        mWorkspaceFolderViewModel.addFolder(
            (activity as WorkspaceActivity).workspace_id!!,
            cwd, folderName, (activity as WorkspaceActivity).token!!
        )
    }
    private fun updateFolderName(oldFolderName: String, folderName: String){
        mWorkspaceFolderViewModel.updateFolderName(
            (activity as WorkspaceActivity).workspace_id!!,
            "$cwd/$oldFolderName",
            folderName,
            (activity as WorkspaceActivity).token!!
        )
    }
    private fun deleteFolder(folderName: String){
        mWorkspaceFolderViewModel.deleteFolder(
            (activity as WorkspaceActivity).workspace_id!!,
            "$cwd/$folderName",
            folderName,
            (activity as WorkspaceActivity).token!!
        )
    }

    override fun onEditFileClicked(fileName: String) {
        //TODO("Not yet implemented")
    }

    override fun onFileNameClicked(fileName: String) {
        //TODO("Not yet implemented")
    }

    override fun onDeleteFileClicked(fileName: String) {
        AlertDialog.Builder(requireContext()).setMessage("This cannot be undone!").setPositiveButton("Delete") {_,_->
            mWorkspaceFolderViewModel.deleteFile(
                (activity as WorkspaceActivity).workspace_id!!,
                cwd,
                fileName,
                (activity as WorkspaceActivity).token!!
            )
        }.setCancelable(true).setNegativeButton("No", null).show()


    }

}
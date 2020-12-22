package com.cmpe451.platon.page.activity.workspace.fragment.editworkspace

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentEditWorkspaceBinding
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.page.activity.workspace.fragment.workspace.WorkspaceViewModel
import com.cmpe451.platon.util.Definitions
import java.util.*

class EditWorkspaceFragment : Fragment() {

    private lateinit var binding: FragmentEditWorkspaceBinding
    private lateinit var dialog: AlertDialog
    private val mWorkspaceViewModel: WorkspaceViewModel by activityViewModels()
    private val mEditWorkspaceViewModel:EditWorkspaceViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditWorkspaceBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setObservers()
        setView()
        setOnClickListeners()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setOnClickListeners() {
        binding.buttonWorkspaceDelete.setOnClickListener {
            mEditWorkspaceViewModel.deleteWorkspace(
                (activity as WorkspaceActivity).workspace_id!!,
                (activity as WorkspaceActivity).token!!
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun setView() {
        binding.wsTitleEt.setText(mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.title)
        binding.wsDescriptionEt.setText(mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.description)
        binding.privateSwitch.isChecked =
            mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.is_private
        binding.wsMaxCollabNumberEt.setText(mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.max_collaborators.toString())
        val x = ArrayAdapter(requireContext(), R.layout.spinner_item, mutableListOf("Loading..."))
        x.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spState.adapter = x
        (binding.spState.adapter as ArrayAdapter<String>).clear()
        (binding.spState.adapter as ArrayAdapter<String>).add(getString(R.string.state_search_for_collab_str))
        (binding.spState.adapter as ArrayAdapter<String>).add(getString(R.string.state_ongoing_str))
        (binding.spState.adapter as ArrayAdapter<String>).add(getString(R.string.state_finished_str))
//        binding.deadlineTv.text = it.data?.deadline
//        binding.wsRequirementsEt.setText(mWorkspaceViewModel.getWorkspaceResponse.value!!.data!!.requirements)
        binding.wsDeadlineEt.setOnTouchListener { _, event ->

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
                        binding.wsDeadlineEt.setText("$years.$monthString.$dayString")
                    }, year, month, dayOfMonth

                )

                datePickerDialog.show()
            }
            true

        }
        binding.buttonWorkspaceEdit.setOnClickListener {
            if (binding.wsTitleEt.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Title cannot be left empty", Toast.LENGTH_LONG)
                    .show()
            } else {
                if (binding.wsDescriptionEt.text.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Description cannot be left empty",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val title: String = binding.wsTitleEt.text.toString().trim()
                    val description: String = binding.wsDescriptionEt.text.toString().trim()
                    val isPrivate: Int = if (binding.privateSwitch.isChecked) 1 else 0
                    val max_collaborators =
                        if (binding.wsMaxCollabNumberEt.text.isNullOrEmpty()) null else binding.wsMaxCollabNumberEt.text.toString()
                            .toInt()
                    val deadline =
                        if (binding.wsDeadlineEt.text.isNullOrEmpty()) null else binding.wsDeadlineEt.text.toString()
                    val state = binding.spState.selectedItemPosition
                    mEditWorkspaceViewModel.updateWorkspace(
                        (activity as WorkspaceActivity).workspace_id!!,
                        title,
                        description,
                        isPrivate,
                        max_collaborators,
                        deadline,
                        null,
                        null,
                        state,
                        (activity as WorkspaceActivity).token!!
                    )
                }
            }
        }
    }


    private fun setObservers() {
        dialog = Definitions().createProgressBar(requireContext())
        mEditWorkspaceViewModel.getDeleteResourceResponse.observe(viewLifecycleOwner, {
            when (it.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Successfully deleted", Toast.LENGTH_LONG)
                        .show()
                    activity?.finish()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })
        mEditWorkspaceViewModel.getUpdateResourceResponse.observe(viewLifecycleOwner, {
            when (it.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Successfully updated", Toast.LENGTH_LONG)
                        .show()
                    mWorkspaceViewModel.fetchWorkspace((activity as WorkspaceActivity).workspace_id!!, (activity as WorkspaceActivity).token!!)
                    findNavController().navigateUp()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })
    }


}
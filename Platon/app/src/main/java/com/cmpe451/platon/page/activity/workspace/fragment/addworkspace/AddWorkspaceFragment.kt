package com.cmpe451.platon.page.activity.workspace.fragment.addworkspace

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentAddWorkspaceBinding
import java.util.*

class AddWorkspaceFragment: Fragment() {

    lateinit var binding:FragmentAddWorkspaceBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentAddWorkspaceBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)


        binding.wsDeadlineEt.setOnTouchListener { _, event ->

            if(event.action == MotionEvent.ACTION_DOWN){
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, years, months, day ->
                        binding.wsDeadlineEt.setText("$day/$months/$years")
                    }, year, month, dayOfMonth

                )

                datePickerDialog.show()
            }
            true

        }



    }

}
package com.cmpe451.platon.page.activity.workspace.fragment.filesystem

import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cmpe451.platon.databinding.EditFileLayoutBinding
import jp.wasabeef.richeditor.RichEditor
import jp.wasabeef.richeditor.RichEditor.OnTextChangeListener


class WorkspaceEditorFragment: Fragment() {
    private lateinit var binding:EditFileLayoutBinding
    private lateinit var mEditor:RichEditor
    private lateinit var mPreview:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = EditFileLayoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEditor = binding.editor
        mPreview = binding.preview
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(Color.RED);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        mEditor.setPadding(10, 10, 10, 10);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Insert text here...");
        //mEditor.setInputEnabled(false);
        setListeners()
        setHasOptionsMenu(true)

    }

    private fun setListeners() {
        mEditor.setOnTextChangeListener {
            mPreview.text = it
        }
    }

}
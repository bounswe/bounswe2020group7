package com.cmpe451.platon.page.activity.home.fragment.profilepage

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.MediaStoreSignature
import com.cmpe451.platon.R
import com.cmpe451.platon.adapter.CommentsAdapter
import com.cmpe451.platon.adapter.SearchElementsAdapter
import com.cmpe451.platon.adapter.SkillsAdapter
import com.cmpe451.platon.adapter.UserProjectsAdapter
import com.cmpe451.platon.core.BaseActivity
import com.cmpe451.platon.databinding.*
import com.cmpe451.platon.listener.PaginationListener
import com.cmpe451.platon.network.Resource
import com.cmpe451.platon.network.models.Comment
import com.cmpe451.platon.network.models.SearchElement
import com.cmpe451.platon.page.activity.home.HomeActivity
import com.cmpe451.platon.page.activity.home.HomeActivityViewModel
import com.cmpe451.platon.page.activity.workspace.WorkspaceActivity
import com.cmpe451.platon.util.Definitions
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ProfilePageFragment : Fragment(),
    UserProjectsAdapter.UserProjectButtonClickListener,
    CommentsAdapter.OnCommentClickedListener,
    SkillsAdapter.OnTagClickedListener{

    private lateinit var binding: FragmentProfilePageBinding
    private val mProfilePageViewModel: ProfilePageViewModel by activityViewModels()
    private val mActivityViewModel: HomeActivityViewModel by activityViewModels()
    private lateinit var paginationListenerResearches:PaginationListener
    private lateinit var paginationListenerComments:PaginationListener


    private val pageSize = 5
    private lateinit var dialog:AlertDialog
    private var maxPageNumberResearch:Int=0
    private var maxPageNumberComment:Int=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfilePageBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapters()
        setListeners()
    }

    private fun initializeAdapters() {
        val layoutManagerProjects = LinearLayoutManager(this.activity)
        paginationListenerResearches = object:PaginationListener(layoutManagerProjects) {
            override fun loadMoreItems() {
                if (maxPageNumberResearch - 1 > currentPage) {
                    isLoading = true
                    currentPage++
                    mProfilePageViewModel.fetchResearch(
                        (activity as HomeActivity).currUserToken,
                        (activity as HomeActivity).currUserId,
                        currentPage,
                        5
                    )
                }

            }

            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        }

        val layoutManagerComments = LinearLayoutManager(this.activity)
        paginationListenerComments = object:PaginationListener(layoutManagerComments, pageSize) {
            override fun loadMoreItems() {
                if (maxPageNumberComment - 1 > currentPage) {
                    isLoading = true
                    currentPage++
                    mProfilePageViewModel.getComments(
                        (activity as HomeActivity).currUserId,
                        (activity as HomeActivity).currUserToken,
                        currentPage,
                        PAGE_SIZE
                    )
                }

            }

            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
            override var currentPage: Int = 0
        }

        val height = resources.displayMetrics.heightPixels

        binding.rvProfilePageProjects.adapter = UserProjectsAdapter(ArrayList(), requireContext(), this)
        binding.rvProfilePageProjects.layoutManager = layoutManagerProjects
        binding.rvProfilePageProjects.layoutParams =
            LinearLayout.LayoutParams(binding.rvProfilePageProjects.layoutParams.width, height / 3)


        binding.rvProfilePageComments.adapter = CommentsAdapter(ArrayList(), requireContext(), this,(activity as HomeActivity).currUserId)
        binding.rvProfilePageComments.layoutManager = layoutManagerComments
        binding.rvProfilePageComments.layoutParams =
            LinearLayout.LayoutParams(binding.rvProfilePageProjects.layoutParams.width, height / 3)
        binding.rvProfilePageComments.addOnScrollListener(paginationListenerComments)


        binding.rvProfilePageProjects.addOnScrollListener(paginationListenerResearches)
        binding.rvProfilePageSkills.adapter = SkillsAdapter(ArrayList(), requireContext(), this)
        binding.rvProfilePageSkills.layoutManager = GridLayoutManager(this.activity, 3)

        dialog = Definitions().createProgressBar(requireContext())
    }


    private fun setListeners() {
        setButtonListeners()
        setObservers()
        setFields()
    }

    private fun setFields(){
        mActivityViewModel.getUserResourceResponse.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    val user = t.data!!
                    binding.tvEmail.text = user.e_mail
                    binding.tvInstitution.text =
                        if (user.institution != "") user.institution else "Institution not specified!"
                    binding.tvJob.text = user.job
                    val naming = user.name + " " + user.surname
                    binding.textNameSurname.text = naming

                    (binding.rvProfilePageProjects.adapter as UserProjectsAdapter).clearElements()


                    mProfilePageViewModel.fetchResearch(
                        (activity as HomeActivity).currUserToken,
                        user.id,
                        0,
                        5
                    )
                    mProfilePageViewModel.getUserSkills(
                        (activity as HomeActivity).currUserId,
                        (activity as HomeActivity).currUserToken
                    )

                    binding.ratingBar.rating = user.rate.toFloat()

                    val df = SimpleDateFormat("hmsS", Locale.getDefault())
                    val formattedDate =  df.format(Date()).toLong()

                    Glide.with(this)
                        .load(Definitions.API_URL + "api" + user.profile_photo)
                        .placeholder(R.drawable.ic_o_logo)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                        .signature(
                            MediaStoreSignature(
                                "image/png",
                                formattedDate,
                                0
                            )
                        )
                        .into(binding.profilePhoto)
                }
                Resource.Error::class.java -> Toast.makeText(
                    activity,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun setObservers() {
        mProfilePageViewModel.getUploadPhotoResourceResponse.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    dialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Update profile photo is successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                    mProfilePageViewModel.getUploadPhotoResourceResponse.value = Resource.Done()
                }
                Resource.Loading::class.java -> dialog.show()
                Resource.Error::class.java -> {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getUploadPhotoResourceResponse.value = Resource.Done()
                }
            }
        })

        mProfilePageViewModel.getResearchesResourceResponse.observe(viewLifecycleOwner, { t ->
                when (t.javaClass) {
                    Resource.Success::class.java -> {
                        maxPageNumberResearch = t.data!!.number_of_pages
                        (binding.rvProfilePageProjects.adapter as UserProjectsAdapter).submitElements(
                            t.data!!.research_info!!
                        )
                        paginationListenerResearches.isLoading = false
                    }
                    Resource.Error::class.java -> {
                        Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        mProfilePageViewModel.getMuteNotificationsResourceResponse.observe(viewLifecycleOwner, {t->
            when (t.javaClass) {
                Resource.Loading::class.java ->dialog.show()
                Resource.Success::class.java -> {
                    mActivityViewModel.fetchUser((activity as HomeActivity).currUserToken)
                    mProfilePageViewModel.getMuteNotificationsResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getMuteNotificationsResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java->dialog.dismiss()
            }
        })

        mProfilePageViewModel.userSkills.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    (binding.rvProfilePageSkills.adapter as SkillsAdapter).submitElements(t.data!!.skills!!.map { it.name })
                }
                Resource.Error::class.java -> {
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }
            }
        })


        mProfilePageViewModel.getEditProfileResourceResponse.observe(viewLifecycleOwner, { t ->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    paginationListenerResearches.currentPage =0
                    mActivityViewModel.fetchUser((activity as HomeActivity).currUserToken)
                    mProfilePageViewModel.getEditProfileResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java ->{
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getEditProfileResourceResponse.value = Resource.Done()

                }
                Resource.Done::class.java->{
                    dialog.dismiss()
                }

            }
        })

        mProfilePageViewModel.getAddResearchResourceResponse.observe(viewLifecycleOwner, { t ->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    Toast.makeText(activity, "Research Information is added!", Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getAddResearchResourceResponse.value  = Resource.Done()
                    paginationListenerResearches.currentPage =0
                    mActivityViewModel.fetchUser((activity as HomeActivity).currUserToken)

                }
                Resource.Error::class.java ->{
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getAddResearchResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java->dialog.dismiss()
            }
        })

    mProfilePageViewModel.getDeleteResearchResourceResponse.observe(viewLifecycleOwner, { t->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    mProfilePageViewModel.getDeleteResearchResourceResponse.value = Resource.Done()
                    paginationListenerResearches.currentPage =0
                    mActivityViewModel.fetchUser((activity as HomeActivity).currUserToken)
                }
                Resource.Error::class.java ->{
                    mProfilePageViewModel.getDeleteResearchResourceResponse.value = Resource.Done()
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }
                Resource.Done::class.java->dialog.dismiss()
            }

        })


        mProfilePageViewModel.getUserComments.observe(viewLifecycleOwner, {t ->
             when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    if(t.data!!.result.isNotEmpty()){
                        (binding.rvProfilePageComments.adapter as CommentsAdapter).submitElements(t.data!!.result)
                    }else{
                        Toast.makeText(requireContext(), "No comment found", Toast.LENGTH_SHORT).show()
                        binding.tvCommentsTitle.performClick()
                    }
                    paginationListenerComments.isLoading = false
                    mProfilePageViewModel.getUserComments.value = Resource.Done()
                }
                Resource.Error::class.java ->{
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getUserComments.value = Resource.Done()
                }
                Resource.Done::class.java->dialog.dismiss()
            }
        })

    mProfilePageViewModel.getEditResearchResourceResponse.observe(viewLifecycleOwner, {t ->
            when(t.javaClass){
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java ->{
                    mProfilePageViewModel.getEditResearchResourceResponse.value = Resource.Done()
                    paginationListenerResearches.currentPage =0
                    mActivityViewModel.fetchUser((activity as HomeActivity).currUserToken)
                }
                Resource.Error::class.java ->{
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getEditResearchResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java->dialog.dismiss()
            }
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode)
        {
            FilePickerConst.REQUEST_CODE_PHOTO->
                if(resultCode== Activity.RESULT_OK && data!=null)
                {
                    val ls = data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)
                    if(!ls.isNullOrEmpty()){
                        uploadProfilePhoto(ls[0])
                    }
                }
        }
    }

    private fun setButtonListeners() {
        binding.profilePhoto.setOnClickListener{

            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PackageManager.PERMISSION_GRANTED)

            if(requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                /*
                val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
                {
                    if (it.resultCode == Activity.RESULT_OK && it.data != null && it.data!!.data != null) {
                        uploadProfilePhoto(it.data!!.data!!)
                    }
                }

                var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
                chooseFile.type ="image/*"
                chooseFile = Intent.createChooser(chooseFile, "Choose a profile photo")
                startForResult.launch(chooseFile)

                 */*/

                FilePickerBuilder.instance
                    .setMaxCount(1)
                    .enableCameraSupport(false)
                    .pickPhoto(this);


            }else{
                Toast.makeText(
                    requireContext(),
                    "Please give read permissions!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.tvCommentsTitle.setOnClickListener{
            when(binding.rvProfilePageComments.visibility){
                View.GONE->{
                    //getComments
                    mProfilePageViewModel.getComments((activity as HomeActivity).currUserId, (activity as HomeActivity).currUserToken, 0, pageSize)
                    //binding.rvProfilePageComments.adapter = CommentsAdapter(arrayListOf(Comment(0,"hehe", "haha", "today", 2.5)), requireContext())
                    binding.rvProfilePageComments.visibility = View.VISIBLE
                }
                View.VISIBLE->{
                    (binding.rvProfilePageComments.adapter as CommentsAdapter).clearElements()
                    binding.rvProfilePageComments.visibility = View.GONE
                }
            }
        }


        binding.buttonFollowers.setOnClickListener {
            findNavController().navigate(
                ProfilePageFragmentDirections.actionProfilePageFragmentToFollowFragment(
                    "follower"
                )
            )
        }

        binding.buttonFollowing.setOnClickListener {
            findNavController().navigate(
                ProfilePageFragmentDirections.actionProfilePageFragmentToFollowFragment(
                    "following"
                )
            )
        }

        binding.infoTitle.setOnClickListener {
            mActivityViewModel.getAllJobs()
            val editBinding = DialogEditProfileBinding.inflate(layoutInflater, binding.root, false)
            val editDialog = AlertDialog.Builder(requireContext())
                .setView(editBinding.root)
                .show()
            editDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            editBinding.buttonEdit.setOnClickListener {
                val jobStr = if (editBinding.etNewJob.text.isEmpty()) editBinding.spJob.selectedItem.toString().trim() else editBinding.etNewJob.text.toString().trim()
                val institution = if (editBinding.etInstitution.text.isEmpty()) "" else editBinding.etInstitution.text.toString().trim()
                editDialog.dismiss()
                mProfilePageViewModel.editProfile(editBinding.firstnameTv,
                    editBinding.lastnameTv, jobStr ,institution, editBinding.privateSwitch.isChecked, null, null, (activity as HomeActivity).currUserToken)
            }

            val user = mActivityViewModel.getUserResourceResponse.value?.data

            if (user != null){
                editBinding.firstnameTv.setText(user.name)
                editBinding.lastnameTv.setText(user.surname)
                editBinding.etInstitution.setText(user.institution)
                editBinding.privateSwitch.isChecked = user.is_private
            }

            mActivityViewModel.getJobListResourceResponse.observe(viewLifecycleOwner,{ t ->
                when (t.javaClass) {
                    Resource.Loading::class.java -> {
                        val x  = ArrayAdapter(requireContext(), R.layout.spinner_item, mutableListOf("Loading..."))
                        x.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        editBinding.spJob.adapter = x
                    }
                    Resource.Success::class.java -> {
                        (editBinding.spJob.adapter as ArrayAdapter<String>).clear()
                        (editBinding.spJob.adapter as ArrayAdapter<String>).addAll(t.data!!.map { it.name })
                        (editBinding.spJob.adapter as ArrayAdapter<String>).add("Not in list")
                        editBinding.spJob.setSelection(t.data!!.map { it.name }.indexOf(user?.job))
                    }
                    Resource.Error::class.java -> {
                        Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                        mActivityViewModel.getJobListResourceResponse.value = Resource.Done()
                    }
                }
            } )


            editBinding.spJob.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (editBinding.spJob.adapter.count-1 == position){
                        editBinding.etNewJob.visibility = View.VISIBLE
                    }else{
                        editBinding.etNewJob.visibility = View.GONE
                        editBinding.etNewJob.text.clear()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            //findNavController().navigate(ProfilePageFragmentDirections.actionProfilePageFragmentToEditProfileFragment())
        }
        binding.projectsTitle.setOnClickListener{
            val addBinding = DialogResearchInfoAddBinding.inflate(layoutInflater, binding.root, false)
            val addDialog = AlertDialog.Builder(requireContext())
                .setView(addBinding.root)
                .show()
            addDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            addBinding.buttonProjectAdd.setOnClickListener {
                    // check if the title and year is empty
                    if(addBinding.projectNameTv.text.isNullOrEmpty() && addBinding.projectYearTv.text.isNullOrEmpty()){
                        Toast.makeText(activity as HomeActivity, "Title and Year cannot be left empty", Toast.LENGTH_LONG).show()
                    }
                    else {
                        when {
                            addBinding.projectNameTv.text.isNullOrEmpty() -> {
                                Toast.makeText(activity, "Title cannot be left empty", Toast.LENGTH_LONG).show()
                            }
                            addBinding.projectYearTv.text.isNullOrEmpty() -> {
                                Toast.makeText(activity , "Year cannot be left empty", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                var description:String? = null
                                if(!addBinding.projectDescriptionTv.text.isNullOrEmpty()){
                                    description = addBinding.projectDescriptionTv.text.toString()
                                }
                                mProfilePageViewModel.addResearchInfo(addBinding.projectNameTv.text.toString(), description,
                                    addBinding.projectYearTv.text.toString().toInt(),
                                    (activity as HomeActivity).currUserToken
                                )
                                addDialog.dismiss()
                            }
                        }
                    }
            }


            addBinding.tvLinkScholar.setOnClickListener {
                addBinding.tvLinkScholar.setTextColor(resources.getColor(R.color.primary_light, requireActivity().theme))
                addBinding.tvLinkResearchGate.setTextColor(resources.getColor(R.color.secondary_purple, requireActivity().theme))
                addBinding.projectNameTv.visibility  =View.GONE
                addBinding.projectDescriptionTv.visibility  =View.GONE
                addBinding.projectYearTv.visibility  =View.GONE
                addBinding.layLinkAccount.visibility = View.VISIBLE
                addBinding.buttonProjectAdd.text = getString(R.string.link_str)
                addBinding.buttonProjectAdd.setOnClickListener{
                    if(addBinding.cbAcceptLink.isChecked){
                        mProfilePageViewModel.editProfile(null, null, null, null, null,
                            addBinding.etLinkUrl, null,
                            (activity as HomeActivity).currUserToken)
                        addDialog.dismiss()
                    }else{
                        Toast.makeText(requireContext(), "You must accept information sharing!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            addBinding.tvLinkResearchGate.setOnClickListener{
                addBinding.tvLinkResearchGate.setTextColor(resources.getColor(R.color.primary_light, requireActivity().theme))
                addBinding.tvLinkScholar.setTextColor(resources.getColor(R.color.secondary_purple, requireActivity().theme))
                addBinding.projectNameTv.visibility  =View.GONE
                addBinding.projectDescriptionTv.visibility  =View.GONE
                addBinding.projectYearTv.visibility  =View.GONE
                addBinding.layLinkAccount.visibility = View.VISIBLE
                addBinding.buttonProjectAdd.text = getString(R.string.link_str)
                addBinding.buttonProjectAdd.setOnClickListener{
                    if(addBinding.cbAcceptLink.isChecked){
                        mProfilePageViewModel.editProfile(null, null, null, null, null,
                            null,addBinding.etLinkUrl,
                            (activity as HomeActivity).currUserToken)
                        addDialog.dismiss()
                    }else{
                        Toast.makeText(requireContext(), "You must accept information sharing!", Toast.LENGTH_SHORT).show()
                    }


                }
            }

        }
        binding.skillsTitle.setOnClickListener{
            mProfilePageViewModel.getAllSkills()
            onAddDeleteSkillClicked()
        }
        binding.textNameSurname.setOnClickListener {
            var notifText = "mute app notifications"
            var notifAllow = 0
            if(!mActivityViewModel.getUserResourceResponse.value!!.data!!.is_notification_allowed){
                notifText = "unmute app notifications"
                notifAllow = 1
            }
            AlertDialog.Builder(context)
                .setMessage("Are you sure you want to ${notifText}?")
                .setPositiveButton("Yes"
                ) { _, _ ->
                    val x = if(mActivityViewModel.getUserResourceResponse.value!!.data!!.is_email_allowed) 1 else 0
                    mProfilePageViewModel.muteNotifications(x,
                        notifAllow, (activity as HomeActivity).currUserToken)
                }
                .setNegativeButton("Cancel"
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        }
        binding.muteEmailNotifIv.setOnClickListener {
            var notifText = "mute e-mail notifications"
            var notifAllow = 0
            if(!mActivityViewModel.getUserResourceResponse.value!!.data!!.is_email_allowed){
                notifText = "unmute e-mail notifications"
                notifAllow = 1
            }
            AlertDialog.Builder(context)
                .setMessage("Are you sure you want to ${notifText}?")
                .setPositiveButton("Yes"
                ) { _, _ ->
                    val x = if(mActivityViewModel.getUserResourceResponse.value!!.data!!.is_notification_allowed) 1 else 0
                    mProfilePageViewModel.muteNotifications(notifAllow,x, (activity as HomeActivity).currUserToken)
                }
                .setNegativeButton("Cancel"
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        }
    }



    private fun uploadProfilePhoto(data: Uri) {
            if((activity as HomeActivity).checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                val path = Definitions().getRealPathFromUri(requireContext(), data)
                val file = File(path)
                val fBody = RequestBody.create(
                    MediaType.parse(requireContext().contentResolver.getType(data)!!),
                    file
                )

                mProfilePageViewModel.uploadPhoto(fBody, (activity as HomeActivity).currUserToken)
                Glide.with(this)
                    .load(data)
                    .placeholder(R.drawable.ic_o_logo)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .circleCrop()
                    .into(binding.profilePhoto)
            }
    }

    private fun onAddDeleteSkillClicked(){
        mProfilePageViewModel.allSkills.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Success::class.java -> {
                    val skillNameList =
                        (binding.rvProfilePageSkills.adapter as SkillsAdapter).getAllElements()
                    val bArray = t.data!!.map { skillNameList.contains(it) }.toBooleanArray()
                    AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setNeutralButton("Add Nonexistent Skill") { _, _ ->
                            val tmpBinding = AddSkillBinding.inflate(
                                layoutInflater,
                                requireView().parent as ViewGroup,
                                false
                            )
                            AlertDialog.Builder(context).setView(tmpBinding.root)
                                .setCancelable(true)
                                .setNegativeButton("Completed") { _, _ ->
                                    mProfilePageViewModel.getAddDeleteSkillResourceResponse.removeObservers(
                                        viewLifecycleOwner
                                    )
                                }
                                .create().show()
                            tmpBinding.btnAddSkill.setOnClickListener {
                                if (!tmpBinding.etNewSkill.text.isNullOrEmpty()) {
                                    mProfilePageViewModel.addSkillToUser(
                                        tmpBinding.etNewSkill.text.toString().trim(),
                                        (activity as HomeActivity).currUserToken
                                    )
                                    tmpBinding.etNewSkill.text.clear()
                                }
                            }
                        }
                        .setNegativeButton("Completed") { _, _ ->
                            mProfilePageViewModel.getAddDeleteSkillResourceResponse.removeObservers(
                                viewLifecycleOwner
                            )
                        }
                        .setMultiChoiceItems(
                            t.data!!.toTypedArray(),
                            bArray
                        ) { _, which, isChecked ->
                            if (isChecked) {
                                mProfilePageViewModel.addSkillToUser(
                                    t.data!![which],
                                    (activity as HomeActivity).currUserToken
                                )
                            } else {
                                mProfilePageViewModel.deleteSkillFromUser(
                                    t.data!![which],
                                    (activity as HomeActivity).currUserToken
                                )
                            }
                        }
                        .create().show()
                    dialog.dismiss()
                    mProfilePageViewModel.allSkills.removeObservers(viewLifecycleOwner)
                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                Resource.Loading::class.java ->
                    dialog.show()
            }
        })



        mProfilePageViewModel.getAddDeleteSkillResourceResponse.observe(viewLifecycleOwner, { t ->
            when (t.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    mProfilePageViewModel.getUserSkills(
                        (activity as HomeActivity).currUserId,
                        (activity as HomeActivity).currUserToken
                    )
                    dialog.dismiss()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.search_btn)?.isVisible = false
        menu.findItem(R.id.notification_btn)?.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }



    override fun onUserProjectButtonClicked(binding: ResearchesCellBinding, position: Int) {
        if (binding.descTrendProjectTv.visibility == View.GONE){
            binding.descTrendProjectTv.visibility = View.VISIBLE
        }else{
            binding.descTrendProjectTv.visibility = View.GONE
        }

        binding.descTrendProjectTv.refreshDrawableState()
        Definitions().vibrate(50, activity as BaseActivity)
    }

    override fun onUserProjectEditClicked(position: Int) {
        val editBinding = DialogResearchInfoEditBinding.inflate(layoutInflater, binding.root, false)

        val editDialog = AlertDialog.Builder(requireContext())
            .setView(editBinding.root)
            .show()
        editDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val currResearch = (binding.rvProfilePageProjects.adapter as UserProjectsAdapter).getElement(position)


        editBinding.projectNameTv.setText(currResearch.title)
        editBinding.projectYearTv.setText(currResearch.year.toString())
        if(currResearch.description.isNotEmpty()){
            editBinding.projectDescriptionTv.setText(currResearch.description)
        }

        editBinding.buttonEdit.setOnClickListener {
            // check if the title and year is empty
            if(editBinding.projectNameTv.text.isNullOrEmpty() && editBinding.projectYearTv.text.isNullOrEmpty()){
                Toast.makeText(activity as HomeActivity, "Title and Year cannot be left empty", Toast.LENGTH_LONG).show()
            }
            else {
                when {
                    editBinding.projectNameTv.text.isNullOrEmpty() -> {
                        Toast.makeText(activity, "Title cannot be left empty", Toast.LENGTH_LONG).show()
                    }
                    editBinding.projectYearTv.text.isNullOrEmpty() -> {
                        Toast.makeText(activity , "Year cannot be left empty", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        var description:String? = null
                        if(!editBinding.projectDescriptionTv.text.isNullOrEmpty()){
                            description = editBinding.projectDescriptionTv.text.toString()
                        }
                        mProfilePageViewModel.editResearchInfo(currResearch.id,
                            editBinding.projectNameTv.text.toString(), description,
                            editBinding.projectYearTv.text.toString().toInt(),
                            (activity as HomeActivity).currUserToken
                        )
                        editDialog.dismiss()

                    }
                }
            }
        }

        editBinding.buttonDelete.setOnClickListener{
            mProfilePageViewModel.deleteResearchInfo(currResearch.id,
                (activity as HomeActivity).currUserToken)
            editDialog.dismiss()
        }

    }

    override fun onDeleteCommentClicked(element: Comment, position: Int) {
        //TODO("Not yet implemented")
    }

    var maxNumberOfTagSearchPages = 0

    /***
     * Function to handle tag search when a tag in profile page is clicked
     * @param model Name of the tag clicked
     * @param position Position of the tag clicked
     */
    override fun onTagClicked(model:String, position: Int) {
        // get all tags related with clicked one, page and perPage can be tuned
        mProfilePageViewModel.getTagSearch("[\"%s\"]".format(model), 0, 20)
        Log.i("Info","[\"%s\"]".format(model))
        // observe result
        mProfilePageViewModel.getTagSearchResourceResponse.observe(viewLifecycleOwner, {

            when (it.javaClass) {
                Resource.Loading::class.java -> dialog.show()
                Resource.Success::class.java -> {
                    maxNumberOfTagSearchPages = it.data?.number_of_pages!!
                    // prepare dialog to show results
                    val tagSearchUserBinding = DialogTagSearchBinding.inflate(layoutInflater, binding.root, false)
                    val tagSearchDialog = AlertDialog.Builder(requireContext())
                        .setView(tagSearchUserBinding.root)
                        .show()
                    tagSearchDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    tagSearchUserBinding.rvTagSearch.adapter = SearchElementsAdapter(
                        it.data!!.result_list as ArrayList<SearchElement>, requireContext(),
                        requireActivity() as HomeActivity,tagSearchDialog
                    )
                    tagSearchUserBinding.rvTagSearch.layoutManager =
                        LinearLayoutManager(requireContext())

                    mProfilePageViewModel.getTagSearchResourceResponse.value = Resource.Done()
                }
                Resource.Error::class.java -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    mProfilePageViewModel.getTagSearchResourceResponse.value = Resource.Done()
                }
                Resource.Done::class.java -> {
                    dialog.dismiss()
                }
            }
        })
    }
}
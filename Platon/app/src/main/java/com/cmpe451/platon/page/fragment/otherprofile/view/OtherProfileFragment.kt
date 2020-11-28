package com.cmpe451.platon.page.fragment.otherprofile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentProfilePageOthersPrivateBinding
import com.cmpe451.platon.networkmodels.UserInfoResponse
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.fragment.otherprofile.contract.OtherProfileContract
import com.cmpe451.platon.page.fragment.otherprofile.model.OtherProfileRepository
import com.cmpe451.platon.page.fragment.otherprofile.presenter.OtherProfilePresenter
import com.cmpe451.platon.util.Definitions

class OtherProfileFragment: Fragment(), OtherProfileContract.View {


    private lateinit var binding: FragmentProfilePageOthersPrivateBinding
    private lateinit var presenter: OtherProfileContract.Presenter
    private lateinit var details: ArrayList<MutableMap<String,String>>
    private lateinit var user : UserInfoResponse


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfilePageOthersPrivateBinding.inflate(inflater)
        details = ArrayList()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        initializePresenter()

        setUser()
    }
    private fun setUser(){
        user = presenter.getUser()
        binding.textNameSurname.text = user.name + " " + user.surname
        when(user.rate){
            1.0 -> {
                binding.firstStarYellow.visibility = View.VISIBLE
                binding.secondStarYellow.visibility = View.GONE
                binding.thirdStarYellow.visibility = View.GONE
                binding.forthStarYellow.visibility = View.GONE
                binding.fifthStarYellow.visibility = View.GONE
                binding.firstGrayStar.visibility = View.VISIBLE
                binding.secondGrayStar.visibility = View.VISIBLE
                binding.thirdGrayStar.visibility = View.VISIBLE
                binding.forthGrayStar.visibility = View.VISIBLE
                binding.fifthGrayStar.visibility = View.GONE
                binding.textRate.visibility = View.GONE
            }
            2.0 -> {
                binding.firstStarYellow.visibility = View.VISIBLE
                binding.secondStarYellow.visibility = View.VISIBLE
                binding.thirdStarYellow.visibility = View.GONE
                binding.forthStarYellow.visibility = View.GONE
                binding.fifthStarYellow.visibility = View.GONE
                binding.firstGrayStar.visibility = View.VISIBLE
                binding.secondGrayStar.visibility = View.VISIBLE
                binding.thirdGrayStar.visibility = View.VISIBLE
                binding.forthGrayStar.visibility = View.GONE
                binding.fifthGrayStar.visibility = View.GONE
                binding.textRate.visibility = View.GONE
            }
            3.0 -> {
                binding.firstStarYellow.visibility = View.VISIBLE
                binding.secondStarYellow.visibility = View.VISIBLE
                binding.thirdStarYellow.visibility = View.VISIBLE
                binding.forthStarYellow.visibility = View.GONE
                binding.fifthStarYellow.visibility = View.GONE
                binding.firstGrayStar.visibility = View.VISIBLE
                binding.secondGrayStar.visibility = View.VISIBLE
                binding.thirdGrayStar.visibility = View.GONE
                binding.forthGrayStar.visibility = View.GONE
                binding.fifthGrayStar.visibility = View.GONE
                binding.textRate.visibility = View.GONE
            }
            4.0 -> {
                binding.firstStarYellow.visibility = View.VISIBLE
                binding.secondStarYellow.visibility = View.VISIBLE
                binding.thirdStarYellow.visibility = View.VISIBLE
                binding.forthStarYellow.visibility = View.VISIBLE
                binding.fifthStarYellow.visibility = View.GONE
                binding.firstGrayStar.visibility = View.VISIBLE
                binding.secondGrayStar.visibility = View.GONE
                binding.thirdGrayStar.visibility = View.GONE
                binding.forthGrayStar.visibility = View.GONE
                binding.fifthGrayStar.visibility = View.GONE
                binding.textRate.visibility = View.GONE
            }
            5.0 -> {
                binding.firstStarYellow.visibility = View.VISIBLE
                binding.secondStarYellow.visibility = View.VISIBLE
                binding.thirdStarYellow.visibility = View.VISIBLE
                binding.forthStarYellow.visibility = View.VISIBLE
                binding.fifthStarYellow.visibility = View.VISIBLE
                binding.firstGrayStar.visibility = View.GONE
                binding.secondGrayStar.visibility = View.GONE
                binding.thirdGrayStar.visibility = View.GONE
                binding.forthGrayStar.visibility = View.GONE
                binding.fifthGrayStar.visibility = View.GONE
                binding.textRate.visibility = View.GONE

            }
            else -> binding.textRate.visibility = View.VISIBLE
        }
    }
    private fun setListeners() {

        binding.buttonFollowers.setOnClickListener {
            presenter.onFollowersButtonClicked()
        }

        binding.buttonFollowing.setOnClickListener {
            presenter.onFollowingButtonClicked()
        }

        binding.buttonFollow.setOnClickListener {
            presenter.onFollowButtonClicked()
            if(binding.buttonFollow.text == "Requested"){
                binding.buttonFollow.setBackgroundColor(ContextCompat.getColor(this.activity as HomeActivity, R.color.secondary_green_transparent))
                binding.buttonFollow.text = "Follow"
            }
            else {
                binding.buttonFollow.setBackgroundColor(ContextCompat.getColor(this.activity as HomeActivity, R.color.primary_dark_lighter))
                binding.buttonFollow.text = "Requested"
            }

        }

        //password.addTextChangedListener(textWatcher)
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
    override fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = OtherProfileRepository(sharedPreferences)
        presenter = OtherProfilePresenter(this, repository, sharedPreferences, (activity as HomeActivity).navController )
    }

}
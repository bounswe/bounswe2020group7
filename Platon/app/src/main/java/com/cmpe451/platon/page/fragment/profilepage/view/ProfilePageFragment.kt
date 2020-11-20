package com.cmpe451.platon.page.fragment.profilepage.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmpe451.platon.R
import com.cmpe451.platon.databinding.FragmentProfilePageBinding
import com.cmpe451.platon.page.activity.HomeActivity
import com.cmpe451.platon.page.activity.LoginActivity
import com.cmpe451.platon.page.fragment.login.contract.LoginContract
import com.cmpe451.platon.page.fragment.login.model.LoginRepository
import com.cmpe451.platon.page.fragment.login.presenter.LoginPresenter
import com.cmpe451.platon.page.fragment.profilepage.contract.ProfilePageContract
import com.cmpe451.platon.page.fragment.profilepage.model.ProfilePageRepository
import com.cmpe451.platon.page.fragment.profilepage.presenter.ProfilePagePresenter
import com.cmpe451.platon.page.fragment.profilepage.view.adapters.FollowerRecyclerViewAdapter
import com.cmpe451.platon.page.fragment.profilepage.view.adapters.ProfilePageRecyclerViewAdapter

class ProfilePageFragment : Fragment(), ProfilePageContract.View {

    private lateinit var binding: FragmentProfilePageBinding
    private lateinit var presenter: ProfilePageContract.Presenter
    private lateinit var details: ArrayList<MutableMap<String,String>>
    override fun setPresenter(presenter: ProfilePageContract.Presenter) {
        TODO("Not yet implemented")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfilePageBinding.inflate(inflater)
        details = ArrayList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        initializePresenter()
        val rvProfilePage = binding.rvProfilePageInfo
        val adapter = ProfilePageRecyclerViewAdapter(ArrayList())
        rvProfilePage.adapter = adapter
        rvProfilePage.layoutManager = LinearLayoutManager(this.activity)
        adapter.submitList(presenter.getProfilePageDetails())
    }
    private fun setListeners() {

        binding.buttonFollowers.setOnClickListener {
            presenter.onFollowersButtonClicked()
        }

        binding.buttonFollowing.setOnClickListener {
            presenter.onFollowingButtonClicked()
        }

        binding.buttonEditProfile.setOnClickListener {
            presenter.onEditProfileButtonClicked()
        }

        //password.addTextChangedListener(textWatcher)
    }
    private fun initializePresenter(){
        val sharedPreferences = requireContext().getSharedPreferences("token_file", 0)
        val repository = ProfilePageRepository(sharedPreferences)
        presenter = ProfilePagePresenter(this, repository, sharedPreferences, (activity as HomeActivity).navController )
    }
}
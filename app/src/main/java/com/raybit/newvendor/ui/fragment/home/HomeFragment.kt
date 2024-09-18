package com.raybit.newvendor.ui.fragment.home

import android.os.Bundle
import android.service.autofill.UserData
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.raybit.newvendor.ui.adapter.CommonFragmentPagerAdapter
import com.raybit.newvendor.ui.adapter.HomeCategoriesAdapter
import com.raybit.newvendor.ui.adapter.HomeDocsAdapter
import com.raybit.newvendor.ui.viewModel.HomeViewModel
import com.raybit.newvendor.utils.CallType
import com.raybit.newvendor.utils.DateTimeUtils
import com.raybit.newvendor.utils.POSITION
import com.pawegio.kandroid.toast
import com.raybit.newvendor.R
import com.raybit.newvendor.base.presentation.fragment.BaseContainerFragment
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.data.models.service.Category
import com.raybit.newvendor.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : BaseContainerFragment<FragmentHomeBinding>(), DateTimeUtils.OnDateSelected {
    private lateinit var adapter: CommonFragmentPagerAdapter
    private lateinit var docsAdapter: HomeDocsAdapter
    private lateinit var catAdapter: HomeCategoriesAdapter
    private lateinit var binding: FragmentHomeBinding
    private lateinit var userData: UserData

    @Inject
    lateinit var mDataStoreHelper: DataStoreHelper
    val viewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            mDataStoreHelper.getCurrentUserLoggedIn().collectLatest {
                if (it) {

                }
            }
        }
        bindObserver()
    }

    private fun bindObserver() {
        viewModel.loading.observe(this, Observer {
//            binding.progressCircular.visible = it
        })

        viewModel.error.observe(this, Observer {
            requireContext().toast(it.message!!)
        })

    }






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        adapter = CommonFragmentPagerAdapter(requireActivity().supportFragmentManager)
        val titles = arrayOf(
            getString(R.string.chat), getString(R.string.calls),
            getString(R.string.video)
        )
        titles.forEachIndexed { index, s ->

            val fragment = RequestFragment(this)
            val bundle = Bundle()
            val callType = when (index) {
                0 -> CallType.CHAT
                1 -> CallType.CALL
                2 -> CallType.VIDEO
                else -> CallType.CHAT
            }
            bundle.putString(POSITION, callType)

            fragment.arguments = bundle
            adapter.addTab(titles[index], fragment)
        }
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = 3
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest {
//                imProfile.loadImage(it?.profile_pic_url!!)
//                tvName.text=it!!.name
//                tvEmail.text=it.email

            }
        }
        binding.tvDate.setOnClickListener {
//            DateTimeUtils.openDatePicker(requireActivity(), this, true, false)
            findNavController().navigate(R.id.action_detail_to_chat)
        }
//        (requireActivity() as HomeActivity)
    }

    fun clickItem(category: Category) {
        findNavController().navigate(
            R.id.action_homeFragment_to_doctors,
            bundleOf("id" to category.id,"name" to category.title)
        )
    }


    override val layoutResourceId: Int
        get() = R.layout.fragment_home

    override fun onDateSelected(date: String) {
        binding.tvDate.text=date
    }

//    override fun onMessageReceive(message: String, event: String) {
//
//    }


}
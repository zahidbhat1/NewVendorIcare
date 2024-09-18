package com.raybit.newvendor.ui.fragment.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope

import com.raybit.newvendor.ui.adapter.TransactionsAdapter
import com.raybit.newvendor.ui.viewModel.HomeViewModel
import com.raybit.newvendor.utils.ProgressDialog
import com.pawegio.kandroid.toast
import com.raybit.newvendor.R
import com.raybit.newvendor.base.presentation.fragment.BaseContainerFragment
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.databinding.FragmentNotificationsBinding
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HistoryFragment : BaseContainerFragment<FragmentNotificationsBinding>() {

    private lateinit var userData: LoginResponse
    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var progressDialog: ProgressDialog

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
            progressDialog.setLoading(it)
        })

        viewModel.error.observe(this, Observer {
            requireContext().toast(it.message!!)
        })
        viewModel.transactions.observe(this, Observer {
            binding.rvNotifications.adapter= TransactionsAdapter(this@HistoryFragment,it)
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        progressDialog = ProgressDialog(requireActivity())
        lifecycleScope.launch {
            mDataStoreHelper.getGsonValue(
                DataStoreConstants.USER_DATA,
                LoginResponse::class.java
            ).collectLatest {
                userData = it!!
            }
        }
        intialize()
        listeners()

    }

    private fun listeners() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun intialize() {
        viewModel.getTransactions(userData.id)
    }


    override val layoutResourceId: Int
        get() = R.layout.fragment_notifications


}
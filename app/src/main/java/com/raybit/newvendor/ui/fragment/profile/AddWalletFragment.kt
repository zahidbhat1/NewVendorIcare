package com.raybit.newvendor.ui.fragment.profile

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope

import com.raybit.newvendor.ui.viewModel.HomeViewModel
import com.raybit.newvendor.utils.ProgressDialog
import com.google.android.material.snackbar.Snackbar
import com.raybit.newvendor.R
import com.raybit.newvendor.base.presentation.fragment.BaseContainerFragment
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.databinding.FragmentTopupBinding
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddWalletFragment : BaseContainerFragment<FragmentTopupBinding>(), View.OnClickListener {

    private lateinit var userData: LoginResponse
    private lateinit var binding: FragmentTopupBinding
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
            val snackbar = Snackbar.make(binding.textView, it.message?:"", Snackbar.LENGTH_LONG)

// Set background color to green
            snackbar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorGreen))

// Set text color if needed
            snackbar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))

// Set the position to top
            val params = snackbar.view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackbar.view.layoutParams = params

            snackbar.show()
            if(it.message=="Money added successfully"){
               requireActivity().onBackPressed()
            }
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
        viewModel.addMoney.observe(viewLifecycleOwner, Observer {
            requireActivity().onBackPressed()
        })
    }

    private fun listeners() {
        binding.btn0.setOnClickListener(this)
        binding.btn1.setOnClickListener(this)
        binding.btn2.setOnClickListener(this)
        binding.btn3.setOnClickListener(this)
        binding.btn4.setOnClickListener(this)
        binding.btn5.setOnClickListener(this)
        binding.btn6.setOnClickListener(this)
        binding.btn7.setOnClickListener(this)
        binding.btn8.setOnClickListener(this)
        binding.btn9.setOnClickListener(this)
        binding.btnDelete.setOnClickListener {
            if (binding.tvAmount.text.toString().length == 1) {
                binding.tvAmount.text = "0"
            } else {
                binding.tvAmount.text = binding.tvAmount.text.toString()
                    .substring(0, binding.tvAmount.text.toString().length - 1);
            }
        }
        binding.ivBack.setOnClickListener {requireActivity().onBackPressed()}
        binding.btnProceed.setOnClickListener {
            var amount=binding.tvAmount.text.toString().toInt()?:0
            if(amount>=100){
                viewModel.addMoney(userData.id,amount.toString())
            }
        }
    }

    private fun intialize() {


    }


    override val layoutResourceId: Int
        get() = R.layout.fragment_topup

    override fun onClick(v: View?) {
        if (binding.tvAmount.text.toString() != "0") {
            binding.tvAmount.text =
                binding.tvAmount.text.toString() + (v as TextView).text.toString()
        } else {
            binding.tvAmount.text = (v as TextView).text.toString()
        }

    }


}
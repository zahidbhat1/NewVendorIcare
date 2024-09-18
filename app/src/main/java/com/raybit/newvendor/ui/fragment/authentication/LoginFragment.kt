package com.raybit.newvendor.ui.fragment.authentication

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController


import com.raybit.newvendor.ui.activity.HomeActivity
import com.raybit.newvendor.ui.viewModel.AuthenticationViewModel
import com.raybit.newvendor.utils.isValidEmail
import com.raybit.newvendor.utils.onSnackbar
import com.google.gson.Gson
import com.pawegio.kandroid.toast
import com.pawegio.kandroid.visible
import com.raybit.newvendor.R
import com.raybit.newvendor.base.presentation.fragment.BaseContainerFragment
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginFragment : BaseContainerFragment<FragmentLoginBinding>() {

    private var role: String="2"
    var showPassword = true
    private val TAG: String = "LoginFragment"
    @Inject
    lateinit var dataStoreHelper: DataStoreHelper

    @Inject
    lateinit var gson: Gson


    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//    /
// MFSDK.setUpActionBar(isShowToolBar = false)
        loginObserver()
        errorObserver()
    }

    private fun errorObserver() {
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.error.observe(this) {
            binding.progressCircular.visible = false
            requireContext().toast(it.message.toString())
//            if (it.status.toString() != Status.NOTFOUND.toString()) {
//                toast(it.status.toString())
//            } else {
//                val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
//                alertDialogBuilder.setMessage(it.message)
//                alertDialogBuilder.setTitle("Alert!")
//                alertDialogBuilder.setCancelable(true)
//
//                alertDialogBuilder.setPositiveButton(
//                    getString(R.string.register)
//                ) { dialog, _ ->
////                    findNavController().navigate(R.id.action_selectLoginSignUpFragment_to_SignUpFragment)
//                    dialog.cancel()
//                }
//
//                val alertDialog: AlertDialog = alertDialogBuilder.create()
//                alertDialog.show()
//            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        setClicks()
        hideKeyboard()
        checkStoredData()


    }



    private fun loginObserver() {
        viewModel.loginData.observe(this, Observer {
            binding.progressCircular.visible = false

            lifecycleScope.launch {
                setUserData(it)

//                val bundle = bundleOf(
//                    "phone_number" to binding.etPhoneNo.text.toString().trim(),
//                    "country_code" to binding.countryPicker.selectedCountryCode.trim()
//                )
//                findNavController().navigate(R.id.login_to_otp, bundle)
            }
        })
    }


    private suspend fun setUserData(userData: LoginResponse) {
        Log.e("TEST:: ",gson.toJson(userData))
        dataStoreHelper.addGsonValue(DataStoreConstants.USER_DATA, gson.toJson(userData))
        dataStoreHelper.setKeyValue(DataStoreConstants.USER_LOGGED_IN, true)

//        if (userData.cart_details != null) {
//            dataStoreHelper.setKeyValue(
//                DataStoreConstants.CART_ID,
//                userData.cart_details.cart_user_id
//            )
//        }
//        if (userData.documents!!.isNotEmpty()) {
//            dataStoreHelper.setKeyValue(DataStoreConstants.USER_DOC_UPLOADED, true)
//
//        }


        requireContext().startActivity(Intent(requireContext(), HomeActivity::class.java))
        requireActivity().finish()

    }
    private fun checkStoredData() {
        lifecycleScope.launch {
            dataStoreHelper.getGsonValue(DataStoreConstants.USER_DATA, LoginResponse::class.java)
                .collect { savedUserData ->
                    if (savedUserData != null) {
                        Log.d(TAG, "Retrieved userData from DataStore: $savedUserData")
                    } else {
                        Log.d(TAG, "No userData found in DataStore")
                    }
                }
        }
    }


    private fun setClicks() {
        binding.tvShow.setOnClickListener {
            if (showPassword) {
                showPassword = false
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.tvShow.text = getString(R.string.hide)

            } else {
                showPassword = true
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.tvShow.text = getString(R.string.show)

            }
        }
        binding.llRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        binding.btnSubmitDoc.setOnClickListener {
            hideKeyboard()
            if (checkValidations()) {
                binding.progressCircular.visible = true
                role="2"
//                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                        return@OnCompleteListener
//                    }
//
//                    // Get new FCM registration token
//                    val token = task.result
//                    Log.e(TAG, token!!)
//                    viewModel.login(
//                        hashMapOf(
//                            "email" to binding.etEmail.text.toString().trim(),
//                            "password" to binding.etPassword.text.toString().trim(),
//                            "device_token" to token
//                        )
//                    )
//                })
                viewModel.login(
                    hashMapOf(
                        "email" to binding.etEmail.text.toString().trim(),
                        "password" to binding.etPassword.text.toString().trim(),
                        "device_token" to "token",
                        "role_id" to role
                    )
                )
//                findNavController().navigate(R.id.login_to_otp)

            }
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
//            findNavController().navigate(R.id.login_to_Login_Signup)
        }
    }


    override val layoutResourceId: Int
        get() = R.layout.fragment_login

    private fun checkValidations(): Boolean {
        when {

//            binding.etPhoneNo.text?.isEmpty()!! -> {
//                binding.root.onSnackbar(getString(R.string.phone_empty))
//                return false
//            }
            !isValidEmail(binding.etEmail.text.toString()) -> {
                binding.root.onSnackbar(getString(R.string.invalid_email))
                return false
            }
            binding.etPassword.text?.isEmpty()!! -> {
                binding.root.onSnackbar(getString(R.string.password_empty))
                return false
            }
            binding.etPassword.text!!.length < 5 -> {
                binding.root.onSnackbar(getString(R.string.password_length))
                return false
            }

            else -> {
                return true
            }
        }
    }
}
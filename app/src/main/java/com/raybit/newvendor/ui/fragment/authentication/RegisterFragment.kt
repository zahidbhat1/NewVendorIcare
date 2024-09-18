package com.raybit.newvendor.ui.fragment.authentication

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import com.raybit.newvendor.ui.activity.HomeActivity
import com.raybit.newvendor.ui.viewModel.AuthenticationViewModel

import com.raybit.newvendor.utils.DateTimeUtils.openDatePicker
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pawegio.kandroid.toast
import com.raybit.newvendor.R
import com.raybit.newvendor.base.presentation.fragment.BaseContainerFragment
import com.raybit.newvendor.data.dataStore.DataStoreConstants
import com.raybit.newvendor.data.dataStore.DataStoreHelper
import com.raybit.newvendor.data.models.login.LoginResponse
import com.raybit.newvendor.databinding.FragmentRegisterBinding
import com.raybit.newvendor.utils.DateTimeUtils
import com.raybit.newvendor.utils.FileUtils
import com.raybit.newvendor.utils.ProgressDialog
import com.raybit.newvendor.utils.isValidEmail
import com.raybit.newvendor.utils.onSnackbar
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RegisterFragment : BaseContainerFragment<FragmentRegisterBinding>(),
    DateTimeUtils.OnDateSelected {
    private var imageBody: MultipartBody.Part? = null
    var SELECT_IMAGE = 3
    private var role: String = "2"
    var showPassword = true
    private val TAG: String = "FragmentRegister"
    private lateinit var progressDialog: ProgressDialog

    @Inject
    lateinit var dataStoreHelper: DataStoreHelper

    @Inject
    lateinit var gson: Gson


    private lateinit var binding: FragmentRegisterBinding
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
        viewModel.loading.observe(this, Observer {
            progressDialog.setLoading(it)
        })
        viewModel.register.observe(this, Observer {
            requireContext().toast("Patient Registered Successfully!")
            requireActivity().onBackPressed()
        })

        viewModel.error.observe(this, Observer {
//            binding.progressCircular.visible = false
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
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        progressDialog= ProgressDialog(requireActivity())
        setClicks()
        hideKeyboard()
    }


    private fun loginObserver() {
//        viewModel.loginData.observe(this, Observer {
//            binding.progressCircular.visible = false
//
//            lifecycleScope.launch {
//                setUserData(it)
////                val bundle = bundleOf(
////                    "phone_number" to binding.etPhoneNo.text.toString().trim(),
////                    "country_code" to binding.countryPicker.selectedCountryCode.trim()
////                )
////                findNavController().navigate(R.id.login_to_otp, bundle)
//            }
//        })
    }


    private suspend fun setUserData(userData: LoginResponse) {
        Log.e("TEST:: ", gson.toJson(userData))
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
        binding.btnRegister.setOnClickListener {
            hideKeyboard()
            if (checkValidations()) {
                if (imageBody != null) {
                    var hashMap = HashMap<String, RequestBody>()
                    hashMap["name"] = RequestBody.create(
                        "text/plain".toMediaTypeOrNull(), binding.etName.text.toString().trim()
                    )
                    hashMap["email"] = RequestBody.create(
                        "text/plain".toMediaTypeOrNull(), binding.etEmail.text.toString().trim()
                    )
                    hashMap["role_id"] = RequestBody.create(
                        "text/plain".toMediaTypeOrNull(), "3"
                    )
                    hashMap["password"] = RequestBody.create(
                        "text/plain".toMediaTypeOrNull(), binding.etPassword.text.toString().trim()
                    )
                    hashMap["phone"] = RequestBody.create(
                        "text/plain".toMediaTypeOrNull(), binding.etPhoneNo.text.toString().trim()
                    )
                    hashMap["country"] = RequestBody.create(
                        "text/plain".toMediaTypeOrNull(), binding.etCountry.text.toString().trim()
                    )
                    hashMap["state"] = RequestBody.create(
                        "text/plain".toMediaTypeOrNull(), binding.etState.text.toString().trim()
                    )
                    hashMap["city"] = RequestBody.create(
                        "text/plain".toMediaTypeOrNull(), binding.etCity.text.toString().trim()
                    )
                    hashMap["dob"] = RequestBody.create(
                        "text/plain".toMediaTypeOrNull(), binding.etDob.text.toString().trim()
                    )
                    hashMap["address"] = RequestBody.create(
                        "text/plain".toMediaTypeOrNull(), binding.etAddress.text.toString().trim()
                    )
                    viewModel.register(hashMap, imageBody!!)
                }
            }
        }
        binding.etDob.setOnClickListener {
            openDatePicker(requireActivity(),this,true,false)
        }
        binding.ivProfile.setOnClickListener {
            hideKeyboard()
            if (hasMediaPermission()) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    SELECT_IMAGE
                )
            } else {
                getMediaPermission()
            }
        }


        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
//            findNavController().navigate(R.id.login_to_Login_Signup)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
//            Log.e("PATH", FileUtils.getPath(data!!.data!!)!!)
            val file = FileUtils.getFile(requireContext(), data?.data)
            Glide.with(this).load(file).into(binding.ivProfile)
            val requestFile: RequestBody =
                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            imageBody = MultipartBody.Part.createFormData(
                "image",
                file.name + Calendar.getInstance().timeInMillis,
                requestFile
            )

        }
    }

    private fun getMediaPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1011
            )

        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1011
            )
        }
    }

    private fun hasMediaPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override val layoutResourceId: Int
        get() = R.layout.fragment_register

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
            binding.etName.text?.isEmpty()!! -> {
                binding.root.onSnackbar(getString(R.string.empty_name))
                return false
            }

            binding.etPhoneNo.text?.isEmpty()!! -> {
                binding.root.onSnackbar(getString(R.string.phone_empty))
                return false
            }
            binding.etCountry.text?.isEmpty()!! -> {
                binding.root.onSnackbar(getString(R.string.country_empty))
                return false
            }
            binding.etState.text?.isEmpty()!! -> {
                binding.root.onSnackbar(getString(R.string.state_empty))
                return false
            }
            binding.etCity.text?.isEmpty()!! -> {
                binding.root.onSnackbar(getString(R.string.city_empty))
                return false
            }
            binding.etAddress.text?.isEmpty()!! -> {
                binding.root.onSnackbar(getString(R.string.address_empty))
                return false
            }
            binding.etDob.text!!.isEmpty() -> {
                binding.root.onSnackbar(getString(R.string.error_date_of_birth))
                return false
            }

            else -> {
                return true
            }
        }
    }

    override fun onDateSelected(date: String) {
        binding.etDob.setText(date)
    }
}
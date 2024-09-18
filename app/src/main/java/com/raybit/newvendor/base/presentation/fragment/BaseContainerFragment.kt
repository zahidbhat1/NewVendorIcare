package com.raybit.newvendor.base.presentation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.raybit.newvendor.R
import com.raybit.newvendor.base.presentation.extensions.toast
import com.raybit.newvendor.data.AppDataManager
import com.raybit.newvendor.data.models.ErrorModel
import com.raybit.newvendor.data.models.Status
import com.raybit.newvendor.ui.activity.AuthenticationActivity
import com.raybit.newvendor.utils.isNetworkConnected

import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class BaseContainerFragment<T : ViewDataBinding> : Fragment() {


    private var mViewDataBinding: T? = null
    private var mRootView: View? = null

    @Inject
     lateinit var appDataManager: AppDataManager

    @get:LayoutRes
    protected abstract val layoutResourceId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)
        mRootView = mViewDataBinding?.root
        return mRootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRootView = null
        mViewDataBinding?.lifecycleOwner = null
        mViewDataBinding = null

        System.gc()
    }


    fun handleError(errorModel: ErrorModel){
        lifecycleScope.launch {
            if(errorModel.status == Status.UNAUTHROZIED.name) {
                appDataManager.setUserAsLoggedOut()
                startActivity(Intent(requireContext(), AuthenticationActivity::class.java))
                requireContext().toast(errorModel.message ?: getString(R.string.something_went_wrong))
            } else
                requireContext().toast(errorModel.message ?: getString(R.string.something_went_wrong))
        }

    }

     fun hideKeyboard() {
        val view: View? = requireActivity().currentFocus
        if (view != null) {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            )
        }
    }

    open fun isNetworkConnected(): Boolean {
        return requireContext().isNetworkConnected()
    }

     fun networkError() {
        requireContext().toast(getString(R.string.network_error))
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewDataBinding!!.lifecycleOwner = this
        mViewDataBinding!!.executePendingBindings()
    }

    open fun getViewDataBinding(): T {
        return mViewDataBinding!!
    }


}

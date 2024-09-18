package com.raybit.newvendor.base.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseContainerBottomSheetDialogFragment<T : ViewDataBinding> : BottomSheetDialogFragment() {


    private var mViewDataBinding: T? = null
    private var mRootView: View? = null


    @get:LayoutRes
    protected abstract val layoutResourceId: Int


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

      if(mRootView==null){
          mViewDataBinding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)
          mRootView = mViewDataBinding?.root
      }
        return mRootView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mRootView = null
        mViewDataBinding?.lifecycleOwner = null
        mViewDataBinding = null

        System.gc()
    }


    override fun onResume() {
        super.onResume()
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
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

package com.raybit.newvendor.base.presentation.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raybit.newvendor.base.presentation.extensions.toLiveData


import kotlin.properties.Delegates

abstract class BaseViewModel<ViewState : BaseViewState, ViewAction : BaseAction>(initialState: ViewState) :
    ViewModel() {

    private val stateMutableLiveData = MutableLiveData<ViewState>()
    val stateLiveData = stateMutableLiveData.toLiveData()
    private var stateTimeTravelDebugger: StateTimeTravelDebugger? = null

    val isLoading = ObservableBoolean(false)

    init {

        stateTimeTravelDebugger = StateTimeTravelDebugger(this::class.java.simpleName)

    }

    // Delegate will handle state event deduplication
    // (multiple states of the same type holding the same data will not be dispatched multiple times to LiveData stream)
    protected var state by Delegates.observable(initialState) { _, old, new ->
        stateMutableLiveData.value = new

        if (new != old) {
            stateTimeTravelDebugger?.apply {
                addStateTransition(old, new)
                logLast()
            }
        }
    }



    fun sendAction(viewAction: ViewAction) {
        stateTimeTravelDebugger?.addAction(viewAction)
        state = onReduceState(viewAction)
    }

    fun loadData() {
        onLoadData()
    }

    public fun setIsLoading(isLoading: Boolean) {
        this.isLoading.set(isLoading)
    }

    protected open fun onLoadData() {}

    protected abstract fun onReduceState(viewAction: ViewAction): ViewState
}

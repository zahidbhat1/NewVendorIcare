package com.raybit.newvendor.ui.interactor

import com.raybit.newvendor.data.models.service.ServiceData


interface ServiceSelectedInterFace {
    fun onServiceSelect(service: ServiceData)
}
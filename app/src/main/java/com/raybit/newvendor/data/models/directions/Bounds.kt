package com.codebew.deliveryagent.data.models.directions

import com.raybit.newvendor.data.models.directions.Northeast
import com.raybit.newvendor.data.models.directions.Southwest

data class Bounds(
    val northeast: Northeast,
    val southwest: Southwest
)
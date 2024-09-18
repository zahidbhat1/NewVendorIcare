package com.raybit.newvendor.data.models.directions

import com.codebew.deliveryagent.data.models.directions.Bounds
import com.raybit.newvendor.data.models.directions.Leg
import com.raybit.newvendor.data.models.directions.OverviewPolyline

data class Route(
    val bounds: Bounds,
    val copyrights: String,
    val legs: List<Leg>,
    val overview_polyline: OverviewPolyline,
    val summary: String,
    val warnings: List<Any>,
    val waypoint_order: List<Any>
)
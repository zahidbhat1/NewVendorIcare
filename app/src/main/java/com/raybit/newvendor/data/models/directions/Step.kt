package com.raybit.newvendor.data.models.directions

import com.raybit.newvendor.data.models.directions.Distance
import com.raybit.newvendor.data.models.directions.Duration
import com.raybit.newvendor.data.models.directions.Polyline
import com.raybit.newvendor.data.models.directions.StartLocation

data class Step(
    val distance: Distance,
    val duration: Duration,
    val end_location: StartLocation,
    val html_instructions: String,
    val maneuver: String,
    val polyline: Polyline,
    val start_location: StartLocation,
    val travel_mode: String
)
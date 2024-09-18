package com.raybit.newvendor.data.models.directions

data class Direction(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)
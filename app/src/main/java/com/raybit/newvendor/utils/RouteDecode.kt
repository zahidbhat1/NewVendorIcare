package com.raybit.newvendor.utils

import android.content.Context
import android.util.Log
import com.raybit.newvendor.R

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.raybit.newvendor.data.models.directions.Direction
import com.raybit.newvendor.data.models.directions.Route
import com.raybit.newvendor.data.models.directions.StartLocation
import com.raybit.newvendor.data.models.directions.Step


object RouteDecode {
    public fun getLine(context: Context, it: Direction): PolylineOptions? {
        val points: ArrayList<*>? = null
        val lineOptions: PolylineOptions? = null
        val markerOptions = MarkerOptions()
        val routelist = ArrayList<LatLng>()
        if (it!!.routes.size > 0) {
            var decodelist: ArrayList<LatLng>
            val routeA: Route = it!!.routes[0]
            Log.i("zacharia", "Legs length : " + routeA.legs.size)
            if (routeA.legs.size > 0) {
                val steps: List<Step> = routeA.legs[0].steps
                Log.i("zacharia", "Steps size :" + steps.size)
                var step: Step
                var location: StartLocation
                var polyline: String
                for (element in steps) {
                    step = element
                    location = step.start_location
                    routelist.add(LatLng(location.lat, location.lng))
                    Log.i(
                        "zacharia",
                        "Start Location :" + location.lat.toString() + ", " + location.lng.toString()
                    )
                    polyline = step.polyline.points
                    decodelist = decodePoly(polyline)!!
                    routelist.addAll(decodelist)
                    location = step.end_location
                    routelist.add(LatLng(location.lat, location.lng))
                    Log.i(
                        "zacharia",
                        "Start Location :" + location.lat.toString() + ", " + location.lng.toString()
                    )
                }
            }
        }
        Log.i("zacharia", "routelist size : " + routelist.size)
        if (routelist.size > 0) {
            var rectLine: PolylineOptions = PolylineOptions().width(10F)
                .color(context.resources.getColor(R.color.colorPrimary))
            for (i in routelist.indices) {
                rectLine.add(routelist[i])
            }
            // Adding route on the map
            return rectLine

//            markerOptions.position(toPosition)
//            markerOptions.draggable(true)
//            mMap.addMarker(markerOptions)
        }
        return null
    }

    public fun decodePoly(encoded: String): ArrayList<LatLng>? {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val position = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(position)
        }
        return poly
    }
}
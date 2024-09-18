package com.aamir.icarepro.data.models.carwash

import com.raybit.newvendor.data.models.carwash.Timeing
import java.io.Serializable

class CarWash : Serializable {
    val booking_date: String? = null
    val start_datetime: String? = null
    val car_detail: CarDetail? = null
    val car_id: Int? = null
    val cart_id: Int? = null
    val comment: Any? = null
    val created_at: String? = null
    val id: Int? = null
    val service_details: ServiceDetails? = null
    val service_sub_types_id: Int? = null
    val status: Int? = null
    val time_slots_id: Int? = null
    val timeing: Timeing? = null
    val address: Address? = null
    val updated_at: String? = null
}
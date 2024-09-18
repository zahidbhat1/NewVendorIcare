package com.raybit.newvendor.data.models.service

import com.aamir.icarepro.data.models.carwash.CarDetail
import com.aamir.icarepro.data.models.carwash.ServiceDetails
import java.io.Serializable

class Insurance : Serializable {
    val car_id: Int? = null
    val concernd_person_id: Int? = null
    val created_at: String? = null
    val end_datetime: String? = null
    val id: Int? = null
    val service_sub_types_id: Int? = null
    val start_datetime: String? = null
    val status: Int? = null
    val updated_at: String? = null
    val service_details: ServiceDetails? = null
    val car_detail: CarDetail? = null
}
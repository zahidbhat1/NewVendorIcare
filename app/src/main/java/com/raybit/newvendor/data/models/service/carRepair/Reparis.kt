package com.aamir.icarepro.data.models.service.carRepair

import com.raybit.newvendor.data.models.login.UserAddress
import com.aamir.smartcarservice.data.models.login.car.UserCar
import java.io.Serializable

class Reparis : Serializable {
    val booking_date: Any? = null
    val car: UserCar? = null
    val car_id: Int? = null
    val cart_id: Any? = null
    val comment: String? = null
    val created_at: String? = null
    val file_urls: List<FileData>? = null
    val id: Int? = null
    val type: RepairType? = null
    val offer_id: Any? = null
    val quotes: List<Any>? = null
    val quotes_count: Int? = null
    val status: Int? = null
    val time_slots_id: Any? = null
    val updated_at: String? = null
    val address: UserAddress?=null
}
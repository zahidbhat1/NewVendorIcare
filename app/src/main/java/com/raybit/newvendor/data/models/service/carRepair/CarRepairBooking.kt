package com.aamir.icarepro.data.models.service.carRepair

data class CarRepairBooking(
    val agent_id: Int,
    val car_repaires_id: Int,
    val created_at: String,
    val id: Int,
    val reparis: Reparis,
    val status: Int,
    val agent_status: Int,
    val updated_at: String
)
package com.aamir.smartcarservice.data.models.login.car

import com.aamir.icarepro.data.models.car.CarBrand
import com.raybit.newvendor.data.models.login.User
import com.raybit.newvendor.data.models.car.CarBodyType
import java.io.Serializable

class UserCar : Serializable {
    val car_body_type: CarBodyType? = null
    val car_body_type_id: Int? = null
    val car_brand: CarBrand? = null
    val car_brand_id: Int? = null
    val car_country: CarCountry? = null
    val car_cyclinder: CarCyclinder? = null
    val car_cyclinder_id: Int? = null
    val car_make: CarMake? = null
    val car_make_id: Int? = null
    val car_model: CarModel? = null
    val car_model_id: Int? = null
    val country: String? = null
    val created_at: String? = null
    val id: Int? = null
    val plate_code: String? = null
    val plate_number: String? = null
    val status: Int? = null
    val updated_at: String? = null
    val user_id: Int? = null
    val user: User? = null
}
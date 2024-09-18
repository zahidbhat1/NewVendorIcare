package com.raybit.newvendor.data.dataStore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreConstants {

    const val DataStoreName = "prefs"

    val DB_SECRET = stringPreferencesKey("db_secret")
    val AGENT_DB_SECRET = stringPreferencesKey("agent_db_secret")
    val DB_INFORMATION = stringPreferencesKey("db_information")
    val CURRENCY_INF = stringPreferencesKey("currency_inf")
    val APP_UNIQUE_CODE = stringPreferencesKey("app_unique_code")
    val USER_LOGGED_IN = booleanPreferencesKey("user_logged_in")
    val USER_DOC_UPLOADED = booleanPreferencesKey("user_doc_uploaded")
    val USER_SELECTED_ADDRESS = intPreferencesKey("user_selected_address")
    val USER_SELECTED_ADDRESS_ID = intPreferencesKey("user_selected_address_id")
    val USER_SELECTED_CAR = intPreferencesKey("user_selected_car")
    val USER_SELECTED_CAR_ID = intPreferencesKey("user_selected_car_id")
    val USER_LOGIN_DATA = stringPreferencesKey("user_login_data")
    val USER_ADDRESS = stringPreferencesKey("user_address_data")
    val LAT_LANG = stringPreferencesKey("lat_lang")
    val USER_CARS = stringPreferencesKey("user_cars_data")
    val FEATURE_DATA = stringPreferencesKey("feature_data")
    val DIALOG_TOKEN = stringPreferencesKey("dialog_token")
    val APP_TYPE = intPreferencesKey("app_type")
    val BOOKING_FLOW = stringPreferencesKey("booking_flow")
    val SETTING_DATA = stringPreferencesKey("setting_data")
    val APP_TERMINOLOGY = stringPreferencesKey("app_terminology")
    val GENRIC_SUPPLIERID = intPreferencesKey("genric_supplierId")
    val TERMS_CONDITION = stringPreferencesKey("terms_conditions")
    val GENERIC_SPL_BRANCHID = intPreferencesKey("genric_spl_branchId")
    val RESTAURANT_INF = stringPreferencesKey("restaurant_inf")
    val SCREEN_FLOW = stringPreferencesKey("screen_flow")
    val SELF_PICKUP = stringPreferencesKey("self_pickup")
    val SELECTED_LANGUAGE = intPreferencesKey("selected_language")
    val USER_DATA = stringPreferencesKey("user_data")
    val DEFAULT_BRNACH_ID = stringPreferencesKey("default_branch_id")
    val SUPPLIER_STATUS = booleanPreferencesKey("supplier_status")

    val USER_ID = stringPreferencesKey("user_id")
    val CART_ID = stringPreferencesKey("cart_id")
    val ACCESS_TOKEN = stringPreferencesKey("access_token")

    const val TYPE_STRING = "string"
    const val TYPE_DOUBLE = "double"
    const val TYPE_BOOLEAN = "boolean"
    const val TYPE_FLOAT = "float"
    const val TYPE_INT = "int"
}
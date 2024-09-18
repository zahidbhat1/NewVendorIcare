package com.raybit.newvendor.data.network

class NetworkConstants {

    companion object {

        const val LOGIN = "api/login"
        const val NOTIFY_CALL = "api/callNotify"
        const val AGORA = "https://agora-token-generator-demo.vercel.app/api/main?type=rtc"
        const val ADD_TO_WALLET = "api/add-money/{id}/{amount}"
        const val REGISTER = "api/registerUser"
        const val TRANSACTIONS = "api/user/{userId}/transactions"
        const val UPLOAD_FILE = "api/uploadFile"
        const val UPDATE = "api/updateProfile"
        const val HOME = "api/homeData"
        const val DOCTORS = "api/getDoctors"
        const val CONVERSATIONS = "api/getConversations"
        const val CHATS = "api/getChat"
        const val USER = "api/getProfile"
        const val SERVICES = "/api/user/services"
        const val CLIENTS = "/api/agent/users"
        const val CLIENT = "/api/user"
        const val REPAIRS = "/api/agent/repairs"
        const val REPAIR_STATUS = "/api/repair/status"
        const val QUOTE = "/api/post/quotes"
        const val CAR_WASH = "/api/agent/carwash"
        const val SERVICE_AND_MAINT = "/api/agent/services"
        const val SERVICE = "/api/services"
        const val CAR_INSURANCE = "/api/agent/insurance"
        const val CAR_INSURANCE_COMPLETE = "/api/insurance/complete"
        const val CAR_SERVICE_COMPLETE = "/api/maintenance/complete"
        const val CAR_WASH_ADDON = "/api/sub/services"
        const val NOTIFICATIONS = "/api/notification"
        const val LOGINPHONE = "/api/generate/otp"
        const val VERIFYOTP = "/api/verify/otp"
        const val SIGNUP_PHONE = "/api/signup"
        const val REQUESTS = "api/consultation-requests/"
        const val GET_REQUEST_PATH="{doctor_id}/{type}"
        const val DIRECTION_API = "https://maps.googleapis.com/maps/api/directions/json"


    }

}
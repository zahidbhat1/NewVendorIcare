package com.raybit.newvendor.data.models.clients

import com.raybit.newvendor.data.models.clients.Client
import java.io.Serializable

class ClientsResponse : Serializable {
    val count: Int? = null
    val users: List<Client>? = null
}
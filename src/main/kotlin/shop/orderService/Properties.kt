package shop.orderService

import kotlinx.serialization.Serializable

@Serializable
data class DataBaseProperties(
    val url: String,
    val user: String,
    val password: String,
    val driver: String,
    val mode: String = "open"
)

@Serializable
data class ServerProperties(
    val port: Int = 8000
)

@Serializable
data class ServiceProperties(
    val database: DataBaseProperties,
    val server: ServerProperties
)
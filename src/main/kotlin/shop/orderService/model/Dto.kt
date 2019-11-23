package shop.orderService.model
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class OrderDto(
    val accountId: Int,
    val timestamp: String,
    val collectingStatus: String
)


@Serializable
data class ItemDto(
    val itemId: Int,
    val amount: Int
)

@Serializable
data class OrderCreationDto(
    val accountId: Int,
    val timestamp: String = "",
    val collectingStatus: String = ""
)

@Serializable
data class PaymentCreationDto(
    val accountId: Int,
    val timestamp: String,
    val amount: Float
)
package shop.orderService.model

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object Order : IntIdTable("order") {
    val accountId = integer("account_id")
    val orderTimestamp = datetime("order_timestamp").default(DateTime.now())
    val collectingStatus = bool("collecting_status")
}

object Payment : IntIdTable("payment") {
    val orderId = integer("order_id").references(Order.id)
    val amount = float("amount")
    val timestamp = datetime("timestamp").default(DateTime.now())
}

object OrderItems : Table("order_items") {
    val warehouseId = integer("order_id").primaryKey().references(Order.id)
    val productId = integer("prod_id").primaryKey()
    val amount = integer("amount")
}

fun getSchema() = arrayOf(
    Order,
    Payment,
    OrderItems
)

fun createSchema(db: Database) =
    transaction(db) {
        SchemaUtils.create(*getSchema())
    }

fun dropSchema(db: Database) =
    transaction(db) {
        SchemaUtils.drop(*getSchema())
    }
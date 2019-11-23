package shop.orderService

import io.javalin.Javalin
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import kotlinx.serialization.internal.nullable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import shop.orderService.model.*
import shop.orderService.model.Order.accountId
import java.time.LocalDateTime

fun setupEndpoints(server: Javalin, db: Database): Javalin =
    server
        .get("/orders") { c ->
            c.contentType("application/json").result(
                GlobalScope.future {
                    transaction(db) {
                        Order
                            .selectAll().toList()
                    }.map {
                        OrderDto(
                            it[Order.accountId],
                            it[Order.orderTimestamp].toString(),
                            it[Order.collectingStatus].toString()
                        )
                    }.let {
                        Json(JsonConfiguration.Stable)
                            .stringify(OrderDto.serializer().list, it)
                    }
                })
        }
        .get("/orders/:orderID") { c ->
            c.contentType("application/json").result(
                GlobalScope.future {
                    transaction(db) {
                        Order
                            .leftJoin(OrderItems, { id }, { warehouseId })
                            .select { Order.id.eq(c.pathParam("productId").toInt()) }
                            .toList()
                    }.map {
                                    ItemDto(
                                        it[OrderItems.productId],
                                        it[OrderItems.amount]
                                    )
                        }.let {
                            Json(JsonConfiguration.Stable)
                                .stringify(OrderDto.serializer().list, it)
                        }
                    })
                }
        .post("/orders/:orderID/item") { c ->
            c.result(
                GlobalScope.future {
                    Json.nonstrict.parse(OrderCreationDto.serializer(), c.body())
                        .let { order ->
                            transaction(db) {
                                Order.insert {
                                    it[accountId] = order.accountId,
                                    it[orderTimestamp] = DateTime.now(),
                                    it[collectingStatus] = true
                                }
                            }.let {
                                Json(JsonConfiguration.Stable)
                                    .stringify(ItemDto.serializer(), it)
                            }
                        }
                })
        }
        .delete("/orders/:orderID/item") { c ->
            c.result(
                GlobalScope.future {
                    Json.nonstrict.parse(OrderCreationDto.serializer(), c.body())
                        .let { order ->
                            transaction(db) {
                               // Order.delete ()
                            }.let {
                               //
                            }.let {
                                Json(JsonConfiguration.Stable)
                                    .stringify(ItemDto.serializer(), it)
                            }
                        }
                })
        }
        .put(" orders/:order_id/payment") { c ->
            c.result(
                GlobalScope.future {
                    Json.nonstrict.parse(PaymentCreationDto.serializer(), c.body())
                        .let { payment ->
                            transaction(db) {
                                Payment.insert {
                                    it[accountId] = payment.accountId,
                                    it[timestamp] = DateTime.now(),
                                    it[amount] = payment.amount
                                }
                            }
                                .let { order ->
                                    Order.update({ Order.id.eq(c.pathParam("id").toInt()) }) {
                                        it[collectingStatus] = false
                                    }
                                }.let {
                                    c.body()
                                }
                        }
                }
            )}
                    .put(" orders/:order_id/status/:ststusId") { c ->
                    c.result(
                        GlobalScope.future {
                            Json.nonstrict.parse(PaymentCreationDto.serializer(), c.body())
                                .let { order ->
                                    transaction(db) {
                                        Order.update {
                                            it[collectingStatus] = false;
                                        }
                                    }

                                        }.let {
                                            c.body()
                                        }
                                })
        }
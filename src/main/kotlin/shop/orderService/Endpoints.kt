package shop.orderService

import io.javalin.Javalin
import org.jetbrains.exposed.sql.Database

fun setupEndpoints(server: Javalin, db: Database): Javalin =
    server

package shop.orderService

import io.javalin.Javalin
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import shop.orderService.model.createSchema
import shop.orderService.model.dropSchema
import java.io.File
import kotlin.system.exitProcess

fun getConfigFile(args: Array<String>) =
    File(args.getOrElse(0) { "service-properties.json" })
        .takeIf { it.exists() } ?: Util.exitWithError("Properties file not found", -1)

@UnstableDefault
fun getServiceProperties(args: Array<String>): ServiceProperties =
    runCatching {
        Json.nonstrict.parse(ServiceProperties.serializer(), getConfigFile(args).readText())
    }.getOrElse { Util.exitWithError(it.message ?: "Json error", -2) }


fun createServer(props: ServerProperties, db: Database): Javalin =
    Javalin.create {
        it.enableCorsForAllOrigins().apply { showJavalinBanner = true }
    }.start(props.port).apply { setupEndpoints(this, db) }

fun createDatabaseConnection(props: DataBaseProperties): Database =
    Database.connect(props.url, props.driver, props.user, props.password)
        .also {
            when (props.mode) {
                "open" -> {
                }
                "create" -> {
                    createSchema(it)
                }
                "overwrite" -> {
                    dropSchema(it)
                    createSchema(it)
                }
                "delete" -> {
                    dropSchema(it)
                    exitProcess(0)
                }
                else -> Util.exitWithError(
                    "Unknown database connection mode: ${props.mode}",
                    -4
                )
            }
        }

@UnstableDefault
fun main(args: Array<String>) =
    getServiceProperties(args).runCatching {
        createDatabaseConnection(this.database)
            .also {
                createServer(this.server, it)
            }
    }.onFailure {
        Util.exitWithError(it.message ?: "Unexpected error", -3)
    }.let { Unit }
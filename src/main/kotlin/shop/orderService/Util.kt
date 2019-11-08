package shop.orderService

import kotlin.system.exitProcess

object Util {
    fun exitWithError(message: String, errorCode: Int): Nothing =
        System.err.println(message)
            .run { exitProcess(errorCode) }
}
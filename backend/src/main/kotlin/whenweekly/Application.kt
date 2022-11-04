package whenweekly

import io.ktor.server.application.*
import whenweekly.plugins.configureRouting
import whenweekly.plugins.configureSecurity
import whenweekly.plugins.configureSerialization

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun Application.module() {
    configureRouting()
    configureSecurity()
    configureSerialization()
}
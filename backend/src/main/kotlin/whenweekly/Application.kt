package whenweekly

import io.ktor.server.application.*
import whenweekly.plugins.configureRouting
import whenweekly.plugins.configureSecurity

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
fun Application.module() {
    configureRouting()
    configureSecurity()
    configureRouting()
}
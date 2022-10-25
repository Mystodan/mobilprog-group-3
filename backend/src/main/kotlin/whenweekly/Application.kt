package whenweekly

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import whenweekly.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSecurity()
        configureRouting()
    }.start(wait = true)
}

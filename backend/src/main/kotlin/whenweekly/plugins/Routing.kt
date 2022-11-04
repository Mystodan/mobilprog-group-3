package whenweekly.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import whenweekly.routes.*

fun Application.configureRouting() {
    routing {
        eventRouting()
        userRouting()
    }
}

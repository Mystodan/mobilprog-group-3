package whenweekly.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import whenweekly.routes.eventRouting
import whenweekly.routes.resetRouting
import whenweekly.routes.userRouting

fun Application.configureRouting() {
    routing {
        eventRouting()
        userRouting()
        resetRouting()
    }
}
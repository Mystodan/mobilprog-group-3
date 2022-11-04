package whenweekly.plugins

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import whenweekly.routes.lobbyRouting

fun Application.configureRouting() {
    routing {
        lobbyRouting()
    }
}

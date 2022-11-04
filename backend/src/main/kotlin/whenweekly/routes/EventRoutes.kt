package whenweekly.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.eventRouting() {
    route("/events") {
        get {
            call.respondText("Hello World!")
        }
    }
}

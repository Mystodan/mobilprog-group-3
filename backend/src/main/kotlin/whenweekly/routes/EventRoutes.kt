package whenweekly.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import whenweekly.database.repository.UserDBRepository
import whenweekly.domain.repository.UserRepository

fun Route.eventRouting() {
    route("/events") {
        get {
            call.respondText("Hello World!")
        }
    }
}

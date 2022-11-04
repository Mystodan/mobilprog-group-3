package whenweekly.routes

import whenweekly.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.lobbyRouting() {
    route("/lobby") {
        // Gets all lobbies
        get {
            if (lobbies.isEmpty()){
                call.respondText("No lobbies found", status = HttpStatusCode.NotFound)
            }
            call.respond(lobbies);
        }
        // Creates a new lobby
        post {
            val lobby = call.receive<Lobby>()
            lobbies.add(lobby)
            call.respond(lobby)
        }
    }
}

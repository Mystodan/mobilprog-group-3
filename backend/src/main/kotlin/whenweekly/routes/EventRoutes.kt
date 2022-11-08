package whenweekly.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import whenweekly.database.entities.Event
import whenweekly.database.repository.EventDBRepository
import whenweekly.domain.repository.EventRepository

fun Route.eventRouting() {
    val repository: EventRepository = EventDBRepository()
    route("/events") {
        getEvents(repository)
        getEventById(repository)
        addEvent(repository)
    }
}

fun Route.getEvents(repository: EventRepository){
    get {
        val users = repository.getAllEvents()
        call.respond(
            HttpStatusCode.OK,
            users
        )
    }
}

fun Route.getEventById(repository: EventRepository) {
    get("{id}") {
        val id = call.parameters["id"]?.toInt() ?: 0
        val event = repository.getEventById(id)
        event?.let {
            call.respond(HttpStatusCode.Found, it)
        } ?: call.respond(HttpStatusCode.NotFound, "event not found with id $id")
    }
}

fun Route.addEvent(repository: EventRepository) {
    post {
        val newEvent = call.receive<Event>()
        val addedEvent = repository.addEvent(newEvent)
        call.respond(
            HttpStatusCode.Created,
            addedEvent
        )
    }
}


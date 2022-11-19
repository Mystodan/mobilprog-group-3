package whenweekly.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import whenweekly.database.entities.Event
import whenweekly.database.entities.User
import whenweekly.database.repository.EventDBRepository
import whenweekly.database.repository.UserDBRepository
import whenweekly.domain.repository.EventRepository
import whenweekly.domain.repository.UserRepository
import whenweekly.routes.Constants.EVENTS_ROUTE
import java.util.*


fun Route.eventRouting() {
    val repository: EventRepository = EventDBRepository()
    val userRepository: UserRepository = UserDBRepository()
    route(EVENTS_ROUTE) {
        getEvents(repository, userRepository)
        getEventById(repository)
        addEvent(repository)
        userJoinEvent(repository)
        deleteEvent(repository)
    }
}

fun Route.getEvents(repository: EventRepository, userRepository: UserRepository) {
    get {
        val users = repository.getAllEvents()
        val userId = Shared.getUserId(call.request, userRepository)
        println("userId: $userId")
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

fun Route.userJoinEvent(repository: EventRepository) {
    put("{id}/join") {
        val id = call.parameters["id"]?.toInt() ?: 0
        val user = call.receive<User>()
        val success = repository.addUserToEvent(id, user.id!!)
        if (success) {
            call.respond(HttpStatusCode.OK, "user ${user.id} joined event $id")
        } else {
            call.respond(HttpStatusCode.NotFound, "event not found with id $id")
        }
    }
}


fun Route.deleteEvent(eventRepository: EventRepository){
    delete("{id}") {
        val id = call.parameters["id"]?.toInt() ?: 0
        eventRepository.deleteEventByID(id)

        call.respond(
            HttpStatusCode.OK,
            "Event $id deleted"
        )
    }
}


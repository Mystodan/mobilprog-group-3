package whenweekly.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import whenweekly.database.entities.Event
import whenweekly.database.repository.EventDBRepository
import whenweekly.database.repository.UserDBRepository
import whenweekly.domain.repository.EventRepository
import whenweekly.domain.repository.UserRepository
import whenweekly.routes.Constants.EVENTS_ROUTE
import whenweekly.plugins.dev


// TODO: move logic to repositories.

fun Route.eventRouting() {
    val repository: EventRepository = EventDBRepository()
    val userRepository: UserRepository = UserDBRepository()
    route(EVENTS_ROUTE) {
        dev {
            getEventById(repository)
        }
        getEvents(repository, userRepository)
        addEvent(repository, userRepository)
        userJoinEvent(repository, userRepository)
        deleteEvent(repository, userRepository)
        removeUserFromEvent(repository, userRepository)
    }
}

fun Route.getEvents(repository: EventRepository, userRepository: UserRepository) {
    get {
        val userId = Shared.getUserId(call.request, userRepository)
        println("userId: $userId")

        if (userId == null){
            call.respond(
                HttpStatusCode.Unauthorized,
                "Invalid UUID"
            )
        }
        else {
            val events = repository.getEventsByUserId(userId)
            call.respond(
                HttpStatusCode.OK,
                events
            )
        }
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

fun Route.addEvent(repository: EventRepository,userRepository: UserRepository) {
    post {
        val ownerID = Shared.getUserId(call.request, userRepository)
        if (ownerID == null){
            call.respond(
                HttpStatusCode.Unauthorized,
                "Invalid UUID"
            )
        }
        else {
            val newEvent = call.receive<Event>()
            val owner = userRepository.getUserById(ownerID)
            val addedEvent = repository.addEvent(newEvent, owner!!)
            addedEvent?.let {
                call.respond(
                    HttpStatusCode.Created,
                    it
                )
                // Do we assume the client sent a bad request?
            } ?: call.respond(HttpStatusCode.BadRequest, "event not added")
        }
    }
}

fun Route.userJoinEvent(repository: EventRepository, userRepository: UserRepository) {
    put("{id}/join") {
        val eventId = call.parameters["id"]?.toInt() ?: 0

        val userID = Shared.getUserId(call.request, userRepository )
        if (userID == null){
            call.respond(
                HttpStatusCode.Unauthorized,
                "Invalid UUID"
            )
            return@put
        }

        // Check if event exists
        if (repository.getEventById(eventId) == null) {
            call.respond(HttpStatusCode.NotFound, "event with id $eventId doesn't exist")
            return@put
        }

        val success = repository.addUserToEvent(eventId, userID)
        if (success){
            call.respond(HttpStatusCode.OK, "user $userID joined event $eventId")
        } else {
            call.respond(HttpStatusCode.Conflict, "user $userID is already in event $eventId")
        }
    }
}

interface UserKickRequest : org.ktorm.entity.Entity<UserKickRequest>{
    var userId: Int?
}

fun Route.removeUserFromEvent(eventRepository: EventRepository, userRepository: UserRepository) {
    put("{id}/kick") {
        // Get the event ID from URL
        val eventId = call.parameters["id"]?.toInt() ?: 0

        // Check if event exists
        val event = eventRepository.getEventById(eventId)
        if ( event == null) {
            call.respond(HttpStatusCode.NotFound, "event with id $eventId doesn't exist")
            return@put
        }

        // Get ownerID
        val userID = Shared.getUserId(call.request, userRepository)
        if (userID == null){
            call.respond(
                HttpStatusCode.Unauthorized,
                "Invalid UUID"
            )
            return@put
        }

        if (event.owner!!.id != userID) {
            call.respond(HttpStatusCode.Unauthorized, "You are not the owner of this event")
            return@put
        }

        // get ID of user to remove
        val userToKick = call.receive<UserKickRequest>()
        if (userRepository.getUserById(userToKick.userId!!)==null){
            call.respond(
                HttpStatusCode.NotFound, "Can't find user to remove from event"
            )
        }

        val success = eventRepository.removeUserFromEvent(eventId, userToKick.userId!!)
        if (success){
            call.respond(HttpStatusCode.OK, "user ${userToKick.userId} has been kicked from event $eventId")
        } else {
            call.respond(HttpStatusCode.Conflict, "user ${userToKick.userId} was not in event $eventId")
        }

    }
}


fun Route.deleteEvent(eventRepository: EventRepository, userRepository: UserRepository){
    delete("{id}") {
        val id = call.parameters["id"]?.toInt() ?: 0

        val event = eventRepository.getEventById(id)
        if (event == null) {
            call.respond(HttpStatusCode.NotFound, "Event with id $id not found")
            return@delete
        }

        val userId = Shared.getUserId(call.request, userRepository )
        if (userId == null){
            call.respond(HttpStatusCode.Unauthorized, "Invalid UUID")
            return@delete
        }

        if (event.owner!!.id != userId) {
            call.respond(HttpStatusCode.Unauthorized, "You are not the owner of this event")
            return@delete
        }

        eventRepository.deleteEventByID(id)
        call.respond(
            HttpStatusCode.OK,
            "Event $id deleted"
        )
    }
}


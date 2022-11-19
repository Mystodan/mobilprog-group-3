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
interface UserKickRequest : org.ktorm.entity.Entity<UserKickRequest>{
    var ID:Int
}

fun Route.removeUserFromEvent(eventRepository: EventRepository, userRepository: UserRepository) {
    put("{id}/kick") {
        // Get the event ID from URL
        val eventId = call.parameters["id"]?.toInt() ?: 0

        // Check if event exists
        val event = eventRepository.getEventById(eventId)
        if ( event == null) {
            call.respond(HttpStatusCode.OK, "event with id $eventId doesn't exist")
            return@put
        }

        // Get ownerID
        val userID = Shared.getUserId(call.request, userRepository)
        if (userID == null){
            call.respond(
                HttpStatusCode.NotFound,
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
        if (userRepository.getUserById(userToKick.ID)==null){
            call.respond(
                HttpStatusCode.NotFound, "Can't find user to remove from event"
            )
        }

        val success = eventRepository.removeUserFromEvent(eventId, userToKick.ID)
        if (success){
            call.respond(HttpStatusCode.OK, "user ${userToKick.ID} has been kicked from event $eventId")
        } else {
            call.respond(HttpStatusCode.OK, "user ${userToKick.ID} was not in event $eventId")
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


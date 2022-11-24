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
import whenweekly.plugins.dev
import whenweekly.routes.Constants.EVENTS_ROUTE
import java.time.LocalDate
import whenweekly.routes.UserKickRequest as UserKickRequest1

data class EventWithUsers(
    val event: Event,
    val users: List<User>
)

interface EventJoinRequest : org.ktorm.entity.Entity<EventJoinRequest> {
    var invite_code: String
}

interface UserKickRequest : org.ktorm.entity.Entity<UserKickRequest1> {
    var user_id: Int?
}

// TODO: move logic to repositories.

fun Route.eventRouting() {
    val repository: EventRepository = EventDBRepository()
    val userRepository: UserRepository = UserDBRepository()
    route(EVENTS_ROUTE) {
        dev {
            getEventById(repository)
        }
        getEvents(repository, userRepository)
        joinEvent(repository, userRepository)
        addEvent(repository, userRepository)
        removeUserFromEvent(repository, userRepository)
        deleteEvent(repository, userRepository)
        getAvailableDatesByEventId(repository, userRepository)
    }
}

fun Route.getEvents(repository: EventRepository, userRepository: UserRepository) {
    get {
        val userId = Shared.getUserId(call.request, userRepository)
        if (userId == null) {
            call.respond(
                HttpStatusCode.Unauthorized,
                "Invalid UUID"
            )
        } else {
            val events = repository.getEventsByUserId(userId)

            // This is inefficient, but fine for now.
            // Should get this when getting the events in the future in a single SQL query
            val eventsWithUsers = mutableListOf<EventWithUsers>()
            for (event in events) {
                eventsWithUsers.add(getEventWithUsers(event, userRepository))
            }

            call.respond(
                HttpStatusCode.OK,
                eventsWithUsers
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

fun getEventWithUsers(event: Event, userRepository: UserRepository): EventWithUsers {
    val users = userRepository.getUsersByEventId(event.id)
    for (user in users) {
        // Don't send the uuid's to the client
        user.uuid = null
    }
    return EventWithUsers(event, users)
}

fun Route.addEvent(repository: EventRepository, userRepository: UserRepository) {
    post {
        val ownerID = Shared.getUserId(call.request, userRepository)
        if (ownerID == null) {
            call.respond(
                HttpStatusCode.Unauthorized,
                "Invalid UUID"
            )
        } else {
            val newEvent = call.receive<Event>()
            val owner = userRepository.getUserById(ownerID)
            val addedEvent = repository.addEvent(newEvent, owner!!)
            if (addedEvent == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "event not added"
                )
            } else {
                call.respond(
                    HttpStatusCode.Created,
                    getEventWithUsers(addedEvent, userRepository)
                )
            }
        }
    }
}

fun Route.joinEvent(repository: EventRepository, userRepository: UserRepository) {
    put("/join") {
        val userID = Shared.getUserId(call.request, userRepository)
        if (userID == null) {
            call.respond(
                HttpStatusCode.Unauthorized,
                "Invalid UUID"
            )
            return@put
        }

        val joinRequest = call.receive<EventJoinRequest>()
        val event = repository.getEventByInviteCode(joinRequest.invite_code)
        if (event == null) {
            call.respond(
                HttpStatusCode.NotFound,
                "Event not found with invite code ${joinRequest.invite_code}"
            )
            return@put
        }

        val success = repository.addUserToEvent(event.id, userID)
        if (!success) {
            println("user $userID is already in event ${event.id}")
            call.respond(HttpStatusCode.Conflict, "Already in event")
            return@put
        }
        call.respond(HttpStatusCode.OK, getEventWithUsers(event, userRepository))
    }
}

fun Route.removeUserFromEvent(eventRepository: EventRepository, userRepository: UserRepository) {
    put("{id}/kick") {
        // Get the event ID from URL
        val eventId = call.parameters["id"]?.toInt() ?: 0

        // Check if event exists
        val event = eventRepository.getEventById(eventId)
        if (event == null) {
            call.respond(HttpStatusCode.NotFound, "event with id $eventId doesn't exist")
            return@put
        }

        // Get user id
        val userID = Shared.getUserId(call.request, userRepository)
        if (userID == null) {
            call.respond(
                HttpStatusCode.Unauthorized,
                "Invalid UUID"
            )
            return@put
        }

        // get ID of user to remove
        val userToKick = call.receive<UserKickRequest1>()
        if (userRepository.getUserById(userToKick.user_id!!) == null) {
            call.respond(
                HttpStatusCode.NotFound, "Can't find user to remove from event"
            )
            return@put
        }

        if (userToKick.user_id!! == userID && event.owner!!.id == userID) {
            call.respond(HttpStatusCode.Forbidden, "You can't kick yourself from the event as an owner")
            return@put
        } else if (userToKick.user_id!! != userID && event.owner!!.id != userID) {
            call.respond(HttpStatusCode.Forbidden, "You can't kick other users from the event")
            return@put
        }

        val success = eventRepository.removeUserFromEvent(eventId, userToKick.user_id!!)
        if (success) {
            call.respond(HttpStatusCode.OK, "user ${userToKick.user_id} has been kicked from event $eventId")
        } else {
            call.respond(HttpStatusCode.NotFound, "user ${userToKick.user_id} was not in event $eventId")
        }
    }
}

fun Route.deleteEvent(eventRepository: EventRepository, userRepository: UserRepository) {
    delete("{id}") {
        val id = call.parameters["id"]?.toInt() ?: 0

        val userId = Shared.getUserId(call.request, userRepository)
        if (userId == null) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid UUID")
            return@delete
        }

        val event = eventRepository.getEventById(id)
        if (event == null) {
            call.respond(HttpStatusCode.NotFound, "Event with id $id not found")
            return@delete
        }

        if (event.owner!!.id != userId) {
            call.respond(HttpStatusCode.Unauthorized, "You are not the owner of this event")
            return@delete
        }

        val success = eventRepository.deleteEventByID(id)
        if (success) {
            call.respond(HttpStatusCode.OK, "Event with id $id has been deleted")
        } else {
            call.respond(HttpStatusCode.NotFound, "Event with id $id not found")
        }
    }
}

fun Route.getAvailableDatesByEventId(repository: EventRepository, userRepository: UserRepository) {
    get("{eventId}/availableDates"){
        val eventId = call.parameters["eventId"]?.toInt() ?: 0

        val availableDates = repository.getAvailableDatesByEventId(eventId)
        val eventsWithUsers = mutableListOf<EventWithUsers>()

        val event = repository.getEventById(eventId)
        for (availableDates in availableDates) {
            eventsWithUsers.add(getEventWithUsers(event!!, userRepository))
        }

        call.respond(
            HttpStatusCode.OK,
            eventsWithUsers
        )
    }
}

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
import java.time.LocalDateTime
import whenweekly.routes.UserKickRequest as UserKickRequest1

/**
 * Event with users
 *
 * @property event The event
 * @property users List of users in the event
 * @constructor Create empty Event with users
 */
data class EventWithUsers(
    val event: Event,
    val users: List<User>
)

/**
 * Event join request
 * @property invite_code The invite code for the event
 * @constructor Create empty Event join request
 */
interface EventJoinRequest : org.ktorm.entity.Entity<EventJoinRequest> {
    var invite_code: String
}

/**
 * User kick request
 * @property user_id The user id to kick
 * @constructor Create empty User kick request
 */
interface UserKickRequest : org.ktorm.entity.Entity<UserKickRequest1> {
    var user_id: Int?
}

/**
 * Available dates request
 * @property available_dates The available dates
 * @constructor Create empty Available dates request
 */
interface AvailableDatesRequest : org.ktorm.entity.Entity<AvailableDatesRequest> {
    var available_dates: List<LocalDateTime>?
}

// TODO: move logic to repositories.

/**
 * Event routing
 *
 */
fun Route.eventRouting() {
    // Initialize the repositories
    val repository: EventRepository = EventDBRepository()
    val userRepository: UserRepository = UserDBRepository()

    route(EVENTS_ROUTE) {
        // Dev only route
        dev {
            getEventById(repository)
        }
        getEvents(repository, userRepository)
        joinEvent(repository, userRepository)
        addEvent(repository, userRepository)
        removeUserFromEvent(repository, userRepository)
        deleteEvent(repository, userRepository)
        getAvailableDatesByEventId(repository, userRepository)
        updateAvailableDatesByEventId(repository, userRepository)
    }
}

/**
 * Get events
 *
 * @param repository The event repository
 * @param userRepository The user repository
 */
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

/**
 * Get event by id
 *
 * @param repository The event repository
 */
fun Route.getEventById(repository: EventRepository) {
    get("{id}") {
        val id = call.parameters["id"]?.toInt() ?: 0
        val event = repository.getEventById(id)
        event?.let {
            call.respond(HttpStatusCode.Found, it)
        } ?: call.respond(HttpStatusCode.NotFound, "event not found with id $id")
    }
}

/**
 * Get event with users from event
 * This also nulls the uuid of the users, so they are not sent to the client
 * @param event The event
 * @param userRepository The user repository
 * @return Event with users
 */
fun getEventWithUsers(event: Event, userRepository: UserRepository): EventWithUsers {
    val users = userRepository.getUsersByEventId(event.id)
    for (user in users) {
        // Don't send the uuid's to the client
        user.uuid = null
    }
    return EventWithUsers(event, users)
}

/**
 * Add event
 *
 * @param repository The event repository
 * @param userRepository The user repository
 */
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

/**
 * Join event
 *
 * @param repository The event repository
 * @param userRepository The user repository
 */
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

        // Get the invite code from the request
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

/**
 * Remove user from event
 *
 * @param eventRepository The event repository
 * @param userRepository The user repository
 */
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

        // Make sure owner doesn't kick themselves
        if (userToKick.user_id!! == userID && event.owner!!.id == userID) {
            call.respond(HttpStatusCode.Forbidden, "You can't kick yourself from the event as an owner")
            return@put
        }
        // Make sure user can't kick others
        else if (userToKick.user_id!! != userID && event.owner!!.id != userID) {
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

/**
 * Delete event
 *
 * @param eventRepository The event repository
 * @param userRepository The user repository
 */
fun Route.deleteEvent(eventRepository: EventRepository, userRepository: UserRepository) {
    delete("{id}") {
        val id = call.parameters["id"]?.toInt() ?: 0

        val userId = Shared.getUserId(call.request, userRepository)
        if (userId == null) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid UUID")
            return@delete
        }

        // Get event
        val event = eventRepository.getEventById(id)
        if (event == null) {
            call.respond(HttpStatusCode.NotFound, "Event with id $id not found")
            return@delete
        }

        // Check if user is owner
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

/**
 * Get available dates by event id
 *
 * @param repository The event repository
 * @param userRepository The user repository
 */
fun Route.getAvailableDatesByEventId(repository: EventRepository, userRepository: UserRepository) {
    get("{eventId}/available-dates") {
        val eventId = call.parameters["eventId"]?.toInt() ?: 0

        val userId = Shared.getUserId(call.request, userRepository)
        if (userId == null) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid UUID")
            return@get
        }

        // Check if user has joined event
        userRepository.getUsersByEventId(eventId).all {
            it.id != userId
        }.let {
            if (it) {
                call.respond(HttpStatusCode.Unauthorized, "You are not in this event")
                return@get
            }
        }

        val availableDates = repository.getAvailableDatesByEventId(eventId)

        call.respond(
            HttpStatusCode.OK,
            availableDates
        )
    }
}

/**
 * Update available dates by event id
 *
 * @param repository The event repository
 * @param userRepository The user repository
 */
fun Route.updateAvailableDatesByEventId(repository: EventRepository, userRepository: UserRepository) {
    patch("{eventId}/available-dates") {
        val eventId = call.parameters["eventId"]?.toInt() ?: 0

        val userId = Shared.getUserId(call.request, userRepository)
        if (userId == null) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid UUID")
            return@patch
        }

        // Get the event
        val event = repository.getEventById(eventId)
        if (event == null) {
            call.respond(HttpStatusCode.NotFound, "Event with id $eventId not found")
            return@patch
        }

        // Parse the available dates request
        val availableDates = call.receive<AvailableDatesRequest>()

        // Check that dates are in range of event
        val eventStart = event.start_date
        val eventEnd = event.end_date
        availableDates.available_dates!!.all {
            it.isAfter(eventStart) && it.isBefore(eventEnd)
        }.let {
            if (!it) {
                call.respond(HttpStatusCode.BadRequest, "Dates must be in range of event")
                return@patch
            }
        }

        val success = repository.updateAvailableDates(eventId, userId, availableDates.available_dates!!)
        if (success) {
            call.respond(HttpStatusCode.OK, "Available dates for event with id $eventId has been updated")
        } else {
            call.respond(HttpStatusCode.NotFound, "Event with id $eventId not found")
        }
    }
}

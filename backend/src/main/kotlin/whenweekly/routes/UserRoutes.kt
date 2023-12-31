package whenweekly.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import whenweekly.database.entities.User
import whenweekly.database.repository.EventDBRepository
import whenweekly.database.repository.UserDBRepository
import whenweekly.domain.repository.EventRepository
import whenweekly.domain.repository.UserRepository
import whenweekly.plugins.dev
import whenweekly.routes.Constants.USERS_ROUTE

/**
 * User routing
 *
 */
fun Route.userRouting() {
    // Initialize the repositories
    val userRepository: UserRepository = UserDBRepository()
    val eventRepository: EventRepository = EventDBRepository()

    route(USERS_ROUTE) {
        addUser(userRepository)
        getUser(userRepository)
        // Dev only routes
        dev {
            getUsers(userRepository)
            getEventsForUser(eventRepository)
        }
    }
}

/**
 * Get users
 *
 * @param repository
 */
fun Route.getUsers(repository: UserRepository) {
    get {
        val users = repository.getAllUsers()
        call.respond(
            HttpStatusCode.OK,
            users
        )
    }
}

/**
 * Get user
 *
 * @param repository The user repository
 */
fun Route.getUser(repository: UserRepository) {
    get("/me") {
        val userId = Shared.getUserId(call.request, repository)
        if (userId == null) {
            call.respond(
                HttpStatusCode.NotFound,
                "UUID not found"
            )
            return@get
        }

        val user = repository.getUserById(userId)
        if (user == null) {
            call.respond(
                HttpStatusCode.NotFound,
                "User not found"
            )
            return@get
        }
        call.respond(
            HttpStatusCode.OK,
            user
        )
    }
}

/**
 * Add user
 *
 * @param repository The user repository
 */
fun Route.addUser(repository: UserRepository) {
    post {
        val newUser = call.receive<User>()
        val addedUser = repository.addUser(newUser)
        addedUser?.let {
            call.respond(HttpStatusCode.Created, addedUser)
        } ?: call.respond(HttpStatusCode.InternalServerError, "user not added")
    }
}

/**
 * Get events for user
 *
 * @param eventRepository The event repository
 */
fun Route.getEventsForUser(eventRepository: EventRepository) {
    get("{id}/events") {
        val id = call.parameters["id"]!!.toInt()
        val events = eventRepository.getEventsByUserId(id)
        call.respond(
            HttpStatusCode.OK,
            events
        )
    }
}


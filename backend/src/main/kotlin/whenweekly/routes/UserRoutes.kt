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
import java.nio.ByteBuffer
import java.util.*

fun Route.userRouting() {
    val userRepository: UserRepository = UserDBRepository()
    val eventRepository: EventRepository = EventDBRepository()
    route(USERS_ROUTE) {
        addUser(userRepository)
        dev {
            getUsers(userRepository)
            getUserById(userRepository)
            getEventsForUser(eventRepository)
            setUUID(userRepository)
        }
    }
}

fun Route.getUsers(repository: UserRepository) {
    get {
        val users = repository.getAllUsers()
        call.respond(
            HttpStatusCode.OK,
            users
        )
    }
}

fun Route.setUUID(repository: UserRepository){
    post{
        val editedUser = call.receive<User>()
        editedUser.uuid = UUID.fromString(call.parameters["UUID"]).asBytes()

        repository.updateUser(editedUser)

        call.respond(
            HttpStatusCode.OK,
            editedUser
        )
    }
}

private fun UUID.asBytes(): ByteArray{
    val b = ByteBuffer.wrap(ByteArray(16))
    b.putLong(this.mostSignificantBits)
    b.putLong(this.leastSignificantBits)
    return b.array()
}

fun Route.getUserById(repository: UserRepository) {
    get("{id}") {
        val id = call.parameters["id"]?.toInt() ?: 0
        val user = repository.getUserById(id)
        user?.let {
            call.respond(HttpStatusCode.Found, it)
        } ?: call.respond(HttpStatusCode.NotFound, "user not found with id $id")
    }
}

fun Route.addUser(repository: UserRepository) {
    post {
        val newUser = call.receive<User>()
        val addedUser = repository.addUser(newUser)
        call.respond(
            HttpStatusCode.Created,
            addedUser
        )
    }
}

fun Route.getEventsForUser(eventRepository: EventRepository) {
    get("{id}/events") {
        val id = call.parameters["id"]?.toInt() ?: 0
        val events = eventRepository.getEventsByUserId(id)
        call.respond(
            HttpStatusCode.OK,
            events
        )
    }
}


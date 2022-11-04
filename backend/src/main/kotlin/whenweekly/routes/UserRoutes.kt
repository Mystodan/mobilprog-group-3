package whenweekly.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import whenweekly.database.repository.UserDBRepository
import whenweekly.domain.models.User
import whenweekly.domain.repository.UserRepository

fun Route.userRouting() {
    val repository: UserRepository = UserDBRepository()
    route("/users") {
        getUsers(repository)
        addUser(repository)
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


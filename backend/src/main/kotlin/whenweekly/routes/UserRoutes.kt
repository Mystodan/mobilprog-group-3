package whenweekly.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import whenweekly.database.entities.User
import whenweekly.database.repository.UserDBRepository
import whenweekly.domain.repository.UserRepository

fun Route.userRouting() {
    val repository: UserRepository = UserDBRepository()
    route("/users") {
        //auth {
            addUser(repository)
            getUsers(repository)
            getUserById(repository)
        //}
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


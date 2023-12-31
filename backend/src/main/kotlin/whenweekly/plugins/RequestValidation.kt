package whenweekly.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import whenweekly.database.entities.Event
import whenweekly.database.entities.User
import whenweekly.routes.AvailableDatesRequest
import whenweekly.routes.EventJoinRequest
import whenweekly.routes.UserKickRequest

/**
 * Configure request validation
 * See https://ktor.io/docs/request-validation.html
 */
fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<User> { user ->
            if (user.name.isBlank()) {
                ValidationResult.Invalid("Name cannot be blank")
            } else {
                ValidationResult.Valid
            }
        }
        validate<Event> { event ->
            if (event.name.isBlank()) {
                ValidationResult.Invalid("Name cannot be blank")
            } else if (event.start_date == null) {
                ValidationResult.Invalid("Start date not specified")
            } else if (event.end_date == null) {
                ValidationResult.Invalid("End date not specified")
            } else if (event.start_date!!.isAfter(event.end_date)) {
                ValidationResult.Invalid("Start date cannot be after end date")
            } else {
                ValidationResult.Valid
            }
        }
        validate<UserKickRequest> { request ->
            if (request.user_id == null) {
                ValidationResult.Invalid("User id not specified")
            } else {
                ValidationResult.Valid
            }
        }
        validate<EventJoinRequest> { request ->
            if (request.invite_code.isBlank()) {
                ValidationResult.Invalid("Invite code cannot be blank")
            } else {
                ValidationResult.Valid
            }
        }
        validate<AvailableDatesRequest> {
            if (it.available_dates == null) {
                ValidationResult.Invalid("Dates not specified")
            } else if (it.available_dates!!.isEmpty()) {
                ValidationResult.Invalid("Dates cannot be empty")
            } else {
                ValidationResult.Valid
            }
        }
    }
    install(StatusPages) {
        // Configure status pages for certain exceptions
        exception<RequestValidationException> { call, cause ->
            println("RequestValidationException: ${cause.message}")
            call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
        }
        exception<BadRequestException> { call, cause ->
            println("BadRequestException: ${cause.message}")
            cause.message?.let {
                call.respond(HttpStatusCode.BadRequest, it)
            } ?: call.respond(HttpStatusCode.BadRequest)
        }
        exception<Throwable> { call, cause ->
            println("Throwable: ${cause.message}")
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
}

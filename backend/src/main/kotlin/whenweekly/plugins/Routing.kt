package whenweekly.plugins

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.ktorm.jackson.KtormModule
import whenweekly.routes.*

fun Application.configureRouting() {
    routing {
        eventRouting()
        userRouting()
    }
}
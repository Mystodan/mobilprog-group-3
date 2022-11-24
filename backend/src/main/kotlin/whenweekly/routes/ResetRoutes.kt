package whenweekly.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import whenweekly.database.DatabaseManagerImpl
import whenweekly.domain.manager.DatabaseManager
import whenweekly.plugins.dev
import whenweekly.routes.Constants.BUILD_CONFIG
import whenweekly.routes.Constants.RESET_ROUTE

fun Route.resetRouting() {
    val database: DatabaseManager = DatabaseManagerImpl()

    // Only for development
    dev {
        route(RESET_ROUTE) {
            resetDatabase(database)
        }
    }
}

fun Route.resetDatabase(database: DatabaseManager) {
    delete {
        database.resetDatabase()
        call.respondText("Database reset", status = HttpStatusCode.OK)
    }
}

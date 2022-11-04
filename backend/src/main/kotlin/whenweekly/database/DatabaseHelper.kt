package whenweekly.database

import io.ktor.server.application.*
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.Database
import whenweekly.database.Constants.DATABASE_PASSWORD
import whenweekly.database.Constants.DATABASE_URL
import whenweekly.database.Constants.DATABASE_USERNAME

object DatabaseHelper {
    private var url = ""
    private var username = ""
    private var password = ""

    fun Application.configureDatabaseConfig() {
        url = environment.config.property(DATABASE_URL).getString()
        username = environment.config.property(DATABASE_USERNAME).getString()
        password = environment.config.property(DATABASE_PASSWORD).getString()
    }
    fun connect() = Database.connect(
        url = url,
        user = username,
        password = password
    )
}

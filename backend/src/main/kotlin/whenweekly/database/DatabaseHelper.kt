package whenweekly.database

import io.ktor.server.application.*
import org.ktorm.database.Database
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import whenweekly.database.Constants.DATABASE_PASSWORD
import whenweekly.database.Constants.DATABASE_URL
import whenweekly.database.Constants.DATABASE_USERNAME

object DatabaseHelper {
    private var url = ""
    private var username = ""
    private var password = ""

    /**
     * Configure database config
     *
     */
    fun Application.configureDatabaseConfig() {
        // Url of the database server
        url = environment.config.property(DATABASE_URL).getString()
        // Username of the database server
        username = environment.config.property(DATABASE_USERNAME).getString()
        // Password of the database server
        password = environment.config.property(DATABASE_PASSWORD).getString()
    }

    /**
     * Database instance factory method
     *
     */
    fun database() = Database.connect(
        url = url,
        user = username,
        password = password,
        logger = ConsoleLogger(LogLevel.WARN)
    )
}
